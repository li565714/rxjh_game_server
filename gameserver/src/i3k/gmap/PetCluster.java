package i3k.gmap;

import java.util.HashMap;
import java.util.Map;

public class PetCluster
{

	PetCluster(int roleID)
	{
		this.id = roleID;
		pets = new HashMap<>();
	}

	void addPet(Pet pet)
	{
		pets.put(pet.getID(), pet);
	}

	Pet delPet(int pid)
	{
		return pets.remove(pid);
	}

	int id;
	Map<Integer, Pet> pets;
}
