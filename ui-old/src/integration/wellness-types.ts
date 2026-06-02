export class WellnessTypes {
  static wellnessTypes = [
    {title: "Weight", value: "WEIGHT"},
    {title: "Calories", value: "CALORIES"},
    {title: "Carbohydrates", value: "CARBOHYDRATES"},
    {title: "Protein", value: "PROTEIN"},
    {title: "Fat", value: "FAT"},
  ]

  static getTitle(value: string) {
    return this.wellnessTypes.find(type => type.value === value)?.title
  }
}
