import {app, BrowserWindow, Menu, nativeImage, shell, Tray} from 'electron';
import path from 'path';
import './boot'
import {bootController, initBootController} from './boot/boot-controller';
import {systemEvents} from './events';
import log from 'electron-log';
import {isDev} from "./environment";

if (require('electron-squirrel-startup')) {
  app.quit();
}

log.info("isDev", isDev)

let isQuitting = false; // Controle de fechamento
let tray: Tray | null = null;
let splashWindow: BrowserWindow | null = null;
let mainWindow: BrowserWindow | null = null;

const createTray = () => {
  const iconPath = getIconPath();
  let icon = nativeImage.createFromPath(iconPath);

  // Redimensiona para o padrão do Mac (16x16)
  icon = icon.resize({ width: 16, height: 16 });

  tray = new Tray(icon);

  const contextMenu = Menu.buildFromTemplate([
    { label: 'Show App', click: () => mainWindow?.show() },
    { type: 'separator' },
    { label: 'Quit', click: () => {
        isQuitting = true;
        app.quit();
      }
    }
  ]);

  tray.setToolTip('TP2 Intervals');
  tray.setContextMenu(contextMenu);
};

const getIconPath = () => {
  return app.isPackaged
    ? path.join(process.resourcesPath, 'icon.png')
    : path.join(__dirname, '../../build/icon.png');
};


const getSplashWindowPageUrl = () => {
  if (app.isPackaged) {
    return 'file://' + path.join(__dirname, `../../../browser/assets/loading.html`);
  } else {
    return 'http://localhost:4200/assets/loading.html';
  }
}

const getMainWindowPageUrl = () => {
  if (app.isPackaged) {
    return 'file://' + path.join(__dirname, `../../../browser/index.html`);
  } else {
    return 'http://localhost:4200';
  }
}

const createSplashWindow = async () => {
  splashWindow = new BrowserWindow({
    show: true,
    title: 'Loading',
    width: 500,
    height: 300,
    icon: getIconPath(),
    resizable: false,
    frame: false,
    center: true,
    webPreferences: {
      devTools: false,
      preload: path.join(__dirname, '../preload/index.js'),
    },
  });

  splashWindow.loadURL(getSplashWindowPageUrl())

  splashWindow.on('ready-to-show', () => {
    if (!splashWindow) {
      throw new Error('"splashWindow" is not defined');
    }
    splashWindow.show();
  });

  splashWindow.on('closed', () => {
    splashWindow = null;
  });
};

const createMainWindow = async () => {
  mainWindow = new BrowserWindow({
    show: false,
    width: 800,
    height: 850,
    minWidth: 500,
    minHeight: 450,
    icon: getIconPath(),
    trafficLightPosition: {x: 12, y: 12},
    autoHideMenuBar: true,
    webPreferences: {
      preload: path.join(__dirname, '../preload/index.js'),
    },
  });

  mainWindow.loadURL(getMainWindowPageUrl())

  // Inicializa o ícone da barra de menus
  createTray();

  mainWindow.on('ready-to-show', () => {
    if (!mainWindow) {
      throw new Error('"mainWindow" is not defined');
    }
    if (splashWindow) {
      splashWindow.close();
      splashWindow = null;
    }
    mainWindow.show();
    mainWindow.focus();
  });

  mainWindow.on('close', (event) => {
    if (!isQuitting && process.platform === 'darwin') {
      event.preventDefault();
      mainWindow?.hide(); // Esconde a janela, mas mantém o processo e o @Scheduled ativos
    }
  });

  mainWindow.on('closed', () => {
    if (isQuitting || process.platform !== 'darwin') {
      mainWindow = null;
    }
  });

  // Open urls in the user's browser
  mainWindow.webContents.setWindowOpenHandler((edata) => {
    shell.openExternal(edata.url);
    return {action: 'deny'};
  });

  if (isDev) {
    mainWindow.webContents.openDevTools()
  }

  bootController?.initializeSubscriptions(mainWindow);
  log.transports.console.level = isDev ? 'debug' : 'info'
};

app.whenReady()
  .then(async () => {
    await createSplashWindow();
    systemEvents.on('boot-ready', () => {
      log.info('Creating main window (boot ready event)');
      createMainWindow();
    });
    await initBootController()
  })
  .catch(console.log);

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});
