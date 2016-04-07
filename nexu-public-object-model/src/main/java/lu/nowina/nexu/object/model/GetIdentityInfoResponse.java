/**
 * © Nowina Solutions, 2015-2016
 *
 * Concédée sous licence EUPL, version 1.1 ou – dès leur approbation par la Commission européenne - versions ultérieures de l’EUPL (la «Licence»).
 * Vous ne pouvez utiliser la présente œuvre que conformément à la Licence.
 * Vous pouvez obtenir une copie de la Licence à l’adresse suivante:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Sauf obligation légale ou contractuelle écrite, le logiciel distribué sous la Licence est distribué «en l’état»,
 * SANS GARANTIES OU CONDITIONS QUELLES QU’ELLES SOIENT, expresses ou implicites.
 * Consultez la Licence pour les autorisations et les restrictions linguistiques spécifiques relevant de la Licence.
 */
package lu.nowina.nexu.object.model;

import java.time.LocalDate;
import java.util.Map;

/**
 * Holds data for a response for a get identity info request.
 *
 * <p>Photo is encoded in base 64.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class GetIdentityInfoResponse {

	private String cardDeliveryAuthority;
	private String cardNumber;
	private LocalDate cardValidityDateBegin;
	private LocalDate cardValidityDateEnd;
	private String chipNumber;
	private LocalDate dateOfBirth;
	private String firstName;
	private Gender gender;
	private String middleName;
	private String lastName;
	private String nationality;
	private String nationalNumber;
	private String nobleCondition;
	private String placeOfBirth;
	private String specialStatus;
	
	private String address;
	private String postalCode;
	private String city;
	
	private String photo;
	private String photoMimeType;
	
	private Map<String, IdentityInfoSignatureData> signatureData;
	
	public GetIdentityInfoResponse() {
		super();
	}

	public String getCardDeliveryAuthority() {
		return cardDeliveryAuthority;
	}

	public void setCardDeliveryAuthority(String cardDeliveryAuthority) {
		this.cardDeliveryAuthority = cardDeliveryAuthority;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public LocalDate getCardValidityDateBegin() {
		return cardValidityDateBegin;
	}

	public void setCardValidityDateBegin(LocalDate cardValidityDateBegin) {
		this.cardValidityDateBegin = cardValidityDateBegin;
	}

	public LocalDate getCardValidityDateEnd() {
		return cardValidityDateEnd;
	}

	public void setCardValidityDateEnd(LocalDate cardValidityDateEnd) {
		this.cardValidityDateEnd = cardValidityDateEnd;
	}

	public String getChipNumber() {
		return chipNumber;
	}

	public void setChipNumber(String chipNumber) {
		this.chipNumber = chipNumber;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getNationalNumber() {
		return nationalNumber;
	}

	public void setNationalNumber(String nationalNumber) {
		this.nationalNumber = nationalNumber;
	}

	public String getNobleCondition() {
		return nobleCondition;
	}

	public void setNobleCondition(String nobleCondition) {
		this.nobleCondition = nobleCondition;
	}

	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	public String getSpecialStatus() {
		return specialStatus;
	}

	public void setSpecialStatus(String specialStatus) {
		this.specialStatus = specialStatus;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getPhotoMimeType() {
		return photoMimeType;
	}

	public void setPhotoMimeType(String photoMimeType) {
		this.photoMimeType = photoMimeType;
	}
	
	public Map<String, IdentityInfoSignatureData> getSignatureData() {
		return signatureData;
	}
	
	public void setSignatureData(Map<String, IdentityInfoSignatureData> signatureData) {
		this.signatureData = signatureData;
	}

	public enum Gender {
		MALE,
		FEMALE;
	}
}
