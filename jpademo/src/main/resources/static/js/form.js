
/**
 * Edits the clicked element, by setting the id
 * to the value of the clicked row.
 */
function editClicked(targetInput, entityName, id) {
	return () => { 
		// copy all values from row to actual row
		console.log("copying from ", entityName, id);

		const row = document.getElementById(entityName + "_" + id);
		// note: <tr><td><input> => this returns all inputs that are children of the parent
		const inputRow = targetInput.parentElement.parentElement;
		for (let input of inputRow.querySelectorAll("input")) {
			let idx = input.parentElement.cellIndex
			input.value = row.children[idx].innerText
		}
		return false; 
	} 
}

/**
 * Removes the clicked element, by submitting the form with
 * the id field = -rowId 
 */
function removeClicked(targetInput, entityName, id) {
	return () => { 
		const question = 
			"Do you want to remove the " + entityName + " with ID " + id + "?";
		if ( ! confirm(question)) return false; // user did not confirm
		targetInput.value = -id; 
		return true; 
	}
}

/**
 * Resets the input value to 0 
 */
function addClicked(targetInput) {
	return () => { 
		targetInput.value = 0; return false;
	}
}

/**
 * Adds buttons for each row
 */
function addRowButtons() {
	const buttonsCell = document.createElement('td');
	buttonsCell.innerHTML = 
			"<button class='editrow'>📝</button>"+
			"<button class='rmrow'>🗑</button>";
	document.querySelectorAll(".datarow").forEach(e => {
		const [entityName, id] = e.id.split("_");
		const input = e.parentElement.querySelector("input[name=id]");
		e.parentElement.querySelector("button.addrow").onclick = addClicked(input);
				
		const buttons = buttonsCell.cloneNode(true);
		e.appendChild(buttons);				
		buttons.childNodes[0].onclick = editClicked(input, entityName, id);
		buttons.childNodes[1].onclick = removeClicked(input, entityName, id);
		console.log("appended at ", e, id, input);
	});
}

/**
 * Executes this method only once everything is loaded
 * Calling `addRowButtons` directly would have been a very bad idea (TM), since
 *  no rows would exist at this point yet.
 */
window.onload = addRowButtons;
