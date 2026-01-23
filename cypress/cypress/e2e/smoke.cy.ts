describe('Smoke tests', {
  defaultCommandTimeout: 10000
}, () => {
  beforeEach(() => {
    cy.visit('/')
    cy.get('button[aria-label="Menu"]').click()
  })

  describe('Home Page', () => {
    it('should load home page', () => {
      cy.contains('a', /Home/i).click()
      cy.get('app-home').should('exist')
    })
  })

  describe('Training Peaks', () => {
    it('sync workout visible', () => {
      let mainComponent = 'tp-copy-calendar-to-calendar'

      cy.contains('a', /TrainingPeaks/i).click()
      cy.get('app-training-peaks mat-expansion-panel-header').first().click()

      selectCalendarDate(mainComponent, '11', '17')

      cy.get(mainComponent).find('#btn-confirm').should('exist')
    })

    it('copy plan visible', () => {
      let mainComponent = 'tp-copy-library-container'
      let planName = 'TrainingPeaks Virtual Workouts (TrainingPeaks)'

      cy.contains('a', /TrainingPeaks/i).click()
      cy.get('app-training-peaks mat-expansion-panel-header').eq(1).click()

      cy.get(mainComponent).find('mat-select[formControlName="plan"]').click()
      cy.get('mat-option')
        .contains(planName)
        .click({ force: true })
      cy.get(mainComponent).find('input[formControlName="newName"]')
        .should('have.value', planName)

      cy.get(mainComponent).find('#btn-confirm').should('exist')
    })

    it('copy workouts from calendar to lib visible', () => {
      let mainComponent = 'tp-copy-calendar-to-library'

      cy.contains('a', /TrainingPeaks/i).click()
      cy.get('app-training-peaks mat-expansion-panel-header').eq(2).click()

      selectCalendarDate(mainComponent, '4', '10')

      cy.get(mainComponent)
        .find('mat-form-field#intervals-plan-name')
        .click()
        .type(' ' + new Date().toISOString())

      cy.get(mainComponent).find('#btn-confirm').should('exist')
    })
  })

  xdescribe('Trainer Road', () => {
    it('should copy workout', () => {
      let mainComponent = 'tr-copy-library-to-library'

      cy.contains('a', /TrainerRoad/i).click()
      cy.get('mat-expansion-panel-header').first().click()

      cy.get(mainComponent)
        .find('mat-form-field#tr-workout-name input')
        .should('be.enabled')
      cy.get(mainComponent)
        .find('mat-form-field#tr-workout-name')
        .click()
        .type('Obelisk')
      cy.get('mat-option')
        .contains(`Obelisk`)
        .click()

      cy.get(mainComponent).find('mat-select[formControlName="intervalsPlan"]').click()
      cy.get('mat-option').contains(`tp2intervals`).click()

      cy.get(mainComponent).find('#btn-confirm').should('exist')
    })

    it('should copy workouts from calendar', () => {
      let mainComponent = 'tr-copy-calendar-to-library'

      cy.contains('a', 'Trainer Road').click()
      cy.get('mat-expansion-panel-header').eq(1).click()

      selectCalendarDate(mainComponent, '1', '2')

      cy.get(mainComponent).find('#btn-confirm').should('exist')
    })
  })

  describe('Configuration Page', () => {
    it('should display configuration page', () => {

      cy.contains('a', /Configuration/i).click()
      cy.get('input[formControlName="intervals.api-key"]').should('exist')
      cy.get('input[formControlName="intervals.athlete-id"]').should('exist')
      cy.get('input[formControlName="training-peaks.auth-cookie"]').should('exist')
      cy.get('input[formControlName="trainer-road.auth-cookie"]').should('exist')
    })
  })

  function selectCalendarDate(parentComponent, dayStart, dayEnd) {
    cy.get(parentComponent).find('mat-datepicker-toggle').click()
    cy.get('mat-datepicker-content').find('button.mat-calendar-period-button').click()
    cy.get('mat-datepicker-content').find('button.mat-calendar-body-cell').contains('2024').click()
    cy.get('mat-datepicker-content').find('button.mat-calendar-body-cell').contains('MAR').click()
    cy.get('mat-datepicker-content').find('button.mat-calendar-body-cell').contains(dayStart).click()
    cy.get('mat-datepicker-content').find('button.mat-calendar-body-cell').contains(dayEnd).click()
  }
})
