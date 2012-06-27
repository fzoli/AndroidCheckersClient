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
    }

    public String getName() {
        return getFirstName() == null || getLastName() == null ? null :
               (isInverseName() ? getFirstName() + " " + getLastName() : getLastName() + " " + getFirstName()).trim();
    }
    
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public boolean isInverseName() {
        return inverseName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Sex getSex() {
        return sex;
    }
    
}