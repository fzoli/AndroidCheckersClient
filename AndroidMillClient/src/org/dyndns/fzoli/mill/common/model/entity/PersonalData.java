package org.dyndns.fzoli.mill.common.model.entity;

import java.util.Date;

/**
 *
 * @author zoli
 */
public class PersonalData {
    
    private String firstName, lastName, country, region, city;
    private boolean inverseName;
    private Date birthDate;
    protected Long birth;
    private Sex sex;

    public PersonalData(String firstName, String lastName, boolean inverseName, Date birthDate, Sex sex, String country, String region, String city) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.region = region;
        this.city = city;
        this.inverseName = inverseName;
        this.birthDate = birthDate;
        this.sex = sex;
        if (birthDate != null) birth = birthDate.getTime();
    }

    public String getName() {
        return getFirstName() == null || getLastName() == null ? null :
               (!isInverseName() ? getFirstName() + " " + getLastName() : getLastName() + " " + getFirstName()).trim();
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isInverseName() {
        return inverseName;
    }

    public void setInverseName(boolean inverseName) {
        this.inverseName = inverseName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
        if (birthDate != null) this.birth = birthDate.getTime();
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }
    
    public void clear() {
        setFirstName(null);
        setLastName(null);
        setInverseName(false);
        setSex(null);
        setBirthDate(null);
        setCity(null);
	setRegion(null);
        setCountry(null);
    }
    
}