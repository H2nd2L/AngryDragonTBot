package angryDragon.business.repository;

import angryDragon.business.domain.pet.Pet;

import java.util.List;

public interface PetRepository {
    void addPet(Pet pet);

    Pet findByPetId(String petId);

    Pet findByUserId(String userId);

    List<Pet> returnPetRepository();
}
