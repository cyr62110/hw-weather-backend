package fr.cvlaminck.hwweather.core.external.model.location.country;

public class ExternalCountryResource {

    public String internationalName;

    public String iso2LettersCode;
    public String iso3LettersCode;

    public String phoneNumberPrefixCode;

    public String getInternationalName() {
        return internationalName;
    }

    public void setInternationalName(String internationalName) {
        this.internationalName = internationalName;
    }

    public String getIso2LettersCode() {
        return iso2LettersCode;
    }

    public void setIso2LettersCode(String iso2LettersCode) {
        this.iso2LettersCode = iso2LettersCode;
    }

    public String getIso3LettersCode() {
        return iso3LettersCode;
    }

    public void setIso3LettersCode(String iso3LettersCode) {
        this.iso3LettersCode = iso3LettersCode;
    }

    public String getPhoneNumberPrefixCode() {
        return phoneNumberPrefixCode;
    }

    public void setPhoneNumberPrefixCode(String phoneNumberPrefixCode) {
        this.phoneNumberPrefixCode = phoneNumberPrefixCode;
    }
}
