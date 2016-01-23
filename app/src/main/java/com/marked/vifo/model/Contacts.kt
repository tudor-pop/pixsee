package com.marked.vifo.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * Created by Tudor Pop on 12-Dec-15.
 * Singleton class used to keep all the friends (the list of contacts) of the user
 */
object Contacts {
	val contactList by lazy { ArrayList<Contact>() }

	fun setFriends(contacts: List<Contact>) {
		contactList.clear()
		addContacts(contacts)
	}

	fun addContact(contact: Contact): Boolean {
		contactList.add(contact)
		return true
	}

	fun addContacts(contacts: List<Contact>): Boolean {
		contactList.addAll(contacts)
		return true
	}

	fun removeContact(contact: Contact): Boolean {
		return contactList.remove(contact)
	}

	fun removeContact(position: Int): Contact? {
		if (!contactList.isEmpty())
			return contactList.removeAt(position)
		else
			return null
	}

	fun fromJSONArray(jsonArray: JSONArray): List<Contact> {
		val contacts = ArrayList<Contact>()

		var `object`: JSONObject
		var id: String
		var firstName: String
		var lastName: String
		var token: String
		for (i in 0..jsonArray.length() - 1) {
			`object` = jsonArray.getJSONObject(i)
			id = `object`.getString(Contact.ID)
			firstName = `object`.getString(Contact.FIRST_NAME)
			lastName = `object`.getString(Contact.LAST_NAME)
			token = `object`.getString(Contact.TOKEN)

			contacts.add(Contact(id, firstName, lastName, firstName + " " + lastName, token))
		}

		return contacts
	}

	fun toJSONArray(list: List<Contact>): JSONArray {
		val jsonArray = JSONArray()
		for (contact in list)
			jsonArray.put(contact.toJSON())
		return jsonArray
	}

}
