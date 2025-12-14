package gr.hua.dit.StreetFoodGo.core.service.model;

/**
 * CreatePersonResult DTO
 */
public record CreatePersonResult (
        boolean created,
        String reason,
        PersonView personView
){

    public static CreatePersonResult success(final PersonView personView) {
        if (personView == null) throw new NullPointerException();
        return new CreatePersonResult(true, null, personView);
    }

    public static CreatePersonResult failure(final String reason) {
        if (reason == null) throw new NullPointerException();
        if (reason.isBlank()) throw new IllegalArgumentException();
        return new CreatePersonResult(false, reason, null);
    }
}
