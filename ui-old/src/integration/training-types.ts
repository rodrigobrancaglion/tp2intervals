export class TrainingTypes {
  static trainingTypes = [
    {title: "Day-off (Notes)", value: "DAY_OFF"},
    {title: "Brick", value: "BRICK"},
    {title: "Ride", value: "BIKE"},
    {title: "Virtual Ride", value: "VIRTUAL_BIKE"},
    {title: "MTB", value: "MTB"},
    {title: "Run", value: "RUN"},
    {title: "Swim", value: "SWIM"},
//    {title: "Workout", value: "WORKOUT"},
    {title: "Weight/Strength Training", value: "STRENGTH"},
    {title: "Walk", value: "WALK"},
    {title: "Any other", value: "UNKNOWN"},
  ]

  static getTitle(value: string) {
    return this.trainingTypes.find(type => type.value === value)?.title
  }
}
