
package org.springframework.samples.petclinic.web;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.Clinic;
import org.springframework.samples.petclinic.Owner;
import org.springframework.samples.petclinic.Pet;
import org.springframework.samples.petclinic.PetType;
import org.springframework.samples.petclinic.validation.PetValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

/**
 * JavaBean form controller that is used to add a new <code>Pet</code> to the
 * system.
 * 
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
@SessionAttributes("pet")
public class PetController {

	private final Clinic clinic;


	@Autowired
	public PetController(Clinic clinic) {
		this.clinic = clinic;
	}

	@ModelAttribute("types")
	public Collection<PetType> populatePetTypes() {
		return this.clinic.getPetTypes();
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@RequestMapping(value="/owners/{ownerId}/pets/new",  method = RequestMethod.GET)
	public String initCreationForm(@PathVariable("ownerId") int ownerId, Model model) {
		Owner owner = this.clinic.findOwner(ownerId);
		Pet pet = new Pet();
		owner.addPet(pet);
		model.addAttribute("pet", pet);
		return "pets/createOrUpdatePetForm";
	}

	@RequestMapping(value="/owners/{ownerId}/pets/new", method = RequestMethod.POST)
	public String processCreationForm(@ModelAttribute("pet") Pet pet, BindingResult result, SessionStatus status) {
		new PetValidator().validate(pet, result);
		if (result.hasErrors()) {
			return "pets/createOrUpdatePetForm";
		}
		else {
			this.clinic.storePet(pet);
			status.setComplete();
			return "redirect:/owners/" + pet.getOwner().getId();
		}
	}
	
	@RequestMapping(value="/owners/*/pets/{petId}/edit", method = RequestMethod.GET)
	public String initUpdateForm(@PathVariable("petId") int petId, Model model) {
		Pet pet = this.clinic.findPet(petId);
		model.addAttribute("pet", pet);
		return "pets/createOrUpdatePetForm";
	}

	@RequestMapping(value="/owners/*/pets/{petId}/edit", method = { RequestMethod.PUT, RequestMethod.POST })
	public String processUpdateForm(@ModelAttribute("pet") Pet pet, BindingResult result, SessionStatus status) {
		// we're not using @Valid annotation here because it is easier to define such validation rule in Java
		new PetValidator().validate(pet, result);
		if (result.hasErrors()) {
			return "pets/createOrUpdatePetForm";
		}
		else {
			this.clinic.storePet(pet);
			status.setComplete();
			return "redirect:/owners/" + pet.getOwner().getId();
		}
	}

	@RequestMapping(value="/owners/*/pets/{petId}/edit", method = RequestMethod.DELETE)
	public String deletePet(@PathVariable("petId") int petId) {
		Pet pet = this.clinic.findPet(petId);
		this.clinic.deletePet(petId);
		return "redirect:/owners/" + pet.getOwner().getId();
	}

}