package angryDragon.domain.pet;

import angryDragon.domain.status.Status;

import java.time.LocalDate;

public class Pet {
    private final String userId;
    private LocalDate dateOfCreation = LocalDate.now();
    private final String petName;
    private final String petId;
    private Status petStatus;

    public Pet(String userId, LocalDate dateOfCreation, String petName, String petId, Status petStatus){
        this.userId = userId;
        this.dateOfCreation = dateOfCreation;
        this.petName = petName;
        this.petId = petId;
        this.petStatus = petStatus;
    }

    public String getUserId() {
        return userId;
    }

    public String getPetName(){
        return petName;
    }

    public Status getStatus(){
        return petStatus;
    }

    public String getPetId(){
        return petId;
    }

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }
}