package angryDragon.repository;

import angryDragon.domain.pet.Pet;

public interface PetRepository {
    void addPet(Pet pet);

    Pet findByPetId(String petId);

    Pet findByUserId(String userId);
}
