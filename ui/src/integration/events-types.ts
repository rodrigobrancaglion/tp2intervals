export class EventsTypes {
  static trainingTypes = [
    {title: "Race", value: "RACE"},
  ]

  static getTitle(value: string) {
    return this.trainingTypes.find(type => type.value === value)?.title
  }
}
