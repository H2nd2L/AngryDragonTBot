package angryDragon.business.repository.impl;

import angryDragon.business.domain.pet.Pet;
import angryDragon.business.repository.PetRepository;

import java.util.ArrayList;
import java.util.List;

public class PetRepositoryImpl implements PetRepository {
    private final List<Pet> pets = new ArrayList<>();

    @Override
    public void addPet(Pet pet){
        pets.add(pet);
    }

    @Override
    public Pet findByPetId(String petId){
        for (Pet pet : pets){
            if (pet.getPetId().equals(petId)){
                return pet;
            }
        }
        return null;
    }

    @Override
    public Pet findByUserId(String userId){
        for (Pet pet : pets){
            if (pet.getUserId().equals(userId)){
                return pet;
            }
        }
        return null;
    }

}
