export class ActivitiesTypes {
  static activitiesTypes = [
    {title: "Activity", value: "ACTIVITY"},
    {title: "RPE", value: "RPE"},
    {title: "Feel", value: "FEEL"},
  ]

  static getTitle(value: string) {
    return this.activitiesTypes.find(type => type.value === value)?.title
  }
}
