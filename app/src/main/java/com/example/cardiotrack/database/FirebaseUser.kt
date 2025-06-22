package com.example.cardiotrack.database

import com.example.cardiotrack.domain.Sex
import com.example.cardiotrack.domain.User
import kotlinx.datetime.Instant

enum class FirebaseUserType {
    DOCTOR,
    PATIENT
}

data class FirebaseUser(
    val id: String? = null,
    val doctorId: String? = null,
    val type: FirebaseUserType? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val fullName: String? = null,
    val birthDate: Long? = null,
    val sex: Sex? = null,
    val pesel: String? = null
) {
    companion object {
        fun deserialize(user: FirebaseUser): User {
            checkNotNull(user.type)
            return when (user.type) {
                FirebaseUserType.DOCTOR -> deserializeDoctor(user)
                FirebaseUserType.PATIENT -> deserializePatient(user)
            }
        }

        fun serialize(user: User): FirebaseUser {
            return when (user) {
                is User.Doctor -> serializeDoctor(user)
                is User.Patient -> serializePatient(user)
            }
        }

        fun deserializeDoctor(user: FirebaseUser): User.Doctor {
            checkNotNull(user.id)
            checkNotNull(user.firstName)
            checkNotNull(user.lastName)
            return User.Doctor(
                id = user.id,
                firstName = user.firstName,
                lastName = user.lastName
            )
        }

        fun deserializePatient(user: FirebaseUser): User.Patient {
            checkNotNull(user.id)
            checkNotNull(user.doctorId)
            checkNotNull(user.firstName)
            checkNotNull(user.lastName)
            checkNotNull(user.birthDate)
            checkNotNull(user.sex)
            checkNotNull(user.pesel)
            return User.Patient(
                id = user.id,
                doctorId = user.doctorId,
                firstName = user.firstName,
                lastName = user.lastName,
                birthDate = Instant.fromEpochMilliseconds(user.birthDate),
                sex = user.sex,
                pesel = user.pesel,
            )
        }

        fun serializeDoctor(user: User.Doctor): FirebaseUser {
            return FirebaseUser(
                id = user.id,
                type = FirebaseUserType.DOCTOR,
                firstName = user.firstName,
                lastName = user.lastName
            )
        }

        fun serializePatient(user: User.Patient): FirebaseUser {
            return FirebaseUser(
                id = user.id,
                type = FirebaseUserType.PATIENT,
                doctorId = user.doctorId,
                firstName = user.firstName,
                lastName = user.lastName,
                fullName = "${user.firstName} ${user.lastName}",
                birthDate = user.birthDate.toEpochMilliseconds(),
                sex = user.sex,
                pesel = user.pesel
            )
        }
    }
}