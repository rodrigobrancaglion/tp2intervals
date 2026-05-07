/**
 * EventTypes used in the FE.
 * The user sees only "Race" — the BE maps it to RACE_A/B/C when reading from TP/ICU.
 */
export class EventTypes {
  static eventTypes = [
    {title: "Race", value: "RACE"},
  ]

  static getTitle(value: string) {
    return this.eventTypes.find(type => type.value === value)?.title ?? value
  }
}
