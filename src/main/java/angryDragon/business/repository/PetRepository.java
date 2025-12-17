package angryDragon.business.repository;

import angryDragon.business.domain.pet.Pet;

public interface PetRepository {
    void addPet(Pet pet);

    Pet findByPetId(String petId);

    Pet findByUserId(String userId);
}
