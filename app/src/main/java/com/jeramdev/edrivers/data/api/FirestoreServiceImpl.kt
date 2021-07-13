package com.jeramdev.edrivers.data.api

import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.jeramdev.edrivers.data.model.AchievementModel
import com.jeramdev.edrivers.data.model.DrivingLessonModel
import com.jeramdev.edrivers.data.model.DrivingSchoolModel
import com.jeramdev.edrivers.data.model.UserModel
import com.jeramdev.edrivers.utils.Constants
import com.jeramdev.edrivers.utils.achievementsList
import kotlinx.coroutines.tasks.await

/**
 * Contiene la lógica para trabajar con la base de datos de Firestore Database
 */
class FirestoreServiceImpl : DbService {

    private val db = Firebase.firestore

    override suspend fun addUser(user: UserModel) {
        try {
            db.collection(Constants.USERS_DB).document(user.email).set(user)
            if (user.rol == Constants.STUDENT_ROL) {
                db.collection(Constants.USERS_DB)
                    .document(user.email)
                    .collection(Constants.ACHIEVEMENTS_DB)
                val batch = db.batch()
                achievementsList.forEach { achievement ->
                    val achievementRef = db.collection(Constants.USERS_DB).document(user.email)
                        .collection(Constants.ACHIEVEMENTS_DB).document(achievement.title)
                    batch.set(achievementRef, achievement)
                }
                batch.commit()
            }
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "Error al añadir usuario a la base de datos",
                e.code
            )
        }
    }

    override suspend fun getUser(email: String): UserModel? {
        try {
            return db.collection(Constants.USERS_DB)
                .document(email)
                .get()
                .await()
                .toObject<UserModel>()
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "Error al obtener usuario",
                e.code
            )
        }
    }

    override suspend fun updateTotalPoints(userEmail: String, points: Int) {
        try {
            val currentPoints = db.collection(Constants.USERS_DB).document(userEmail).get().await()
                .toObject<UserModel>()?.totalPoints
            if (currentPoints != null) {
                val newPoints = currentPoints + points
                db.collection(Constants.USERS_DB).document(userEmail)
                    .update("totalPoints", newPoints)
                    .await()
            }
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "Error al actualizar el total de puntos",
                e.code
            )
        }
    }

    override suspend fun getTotalPoints(userEmail: String): Int? {
        try {
            return db.collection(Constants.USERS_DB).document(userEmail).get().await()
                .toObject<UserModel>()?.totalPoints
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "Error al obtener el total de puntos",
                e.code
            )
        }
    }

    override suspend fun getRanking(): List<UserModel> {
        try {
            return db.collection(Constants.USERS_DB)
                .whereEqualTo("rankVisibility", true)
                .whereEqualTo("rol", Constants.STUDENT_ROL)
                .orderBy("totalPoints", Query.Direction.DESCENDING)
                .limit(Constants.USERS_IN_RANKING.toLong())
                .get()
                .await()
                .toObjects()

        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "Error al obtener el ránking",
                e.code
            )
        }
    }

    override suspend fun updateRankVisibility(value: Boolean, userEmail: String) {
        try {
            db.collection(Constants.USERS_DB)
                .document(userEmail)
                .update("rankVisibility", value)
                .await()
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "No se ha podido actualizar la visibilidad en el ránking",
                e.code
            )
        }
    }

    override suspend fun deleteUser(userEmail: String) {
        try {
            db.collection(Constants.USERS_DB)
                .document(userEmail)
                .delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "No se ha podido eliminar la cuenta",
                e.code
            )
        }
    }

    override suspend fun getDrivingSchools(): List<DrivingSchoolModel> {
        try {
            val drivingSchoolList: MutableList<DrivingSchoolModel> = mutableListOf()
            val documents = db.collection(Constants.DRIVING_SCHOOL_DB).get().await().documents
            if (documents.isNotEmpty()) {
                documents.forEach { document ->
                    val drivingSchool = document.toObject<DrivingSchoolModel>()
                    if (drivingSchool != null) {
                        drivingSchoolList.add(drivingSchool)
                    }
                }
            }
            return drivingSchoolList
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "Error al obtener la lista de autoescuelas",
                e.code
            )
        }
    }

    override suspend fun getDrivingSchoolsNames(): List<String> {
        try {
            val drivingSchoolList: MutableList<String> = mutableListOf()
            val documents = db.collection(Constants.DRIVING_SCHOOL_DB).get().await().documents
            if (documents.isNotEmpty()) {
                documents.forEach { document ->
                    val drivingSchool = document.toObject<DrivingSchoolModel>()
                    if (drivingSchool != null) {
                        drivingSchoolList.add(drivingSchool.name)
                    }
                }
            }
            return drivingSchoolList
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "Error al obtener la lista de nombres de las autoescuelas",
                e.code
            )
        }
    }

    override suspend fun getDrivingSchoolByName(drivingSchoolName: String): DrivingSchoolModel? {
        try {
            return db.collection(Constants.DRIVING_SCHOOL_DB)
                .document(drivingSchoolName)
                .get()
                .await()
                .toObject<DrivingSchoolModel>()
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "No se ha encontrado la autoescuela",
                e.code
            )
        }
    }

    // Prácticas
    override suspend fun getDrivingLessonsByStudent(student: String): List<DrivingLessonModel> {
        try {
            return db.collection(Constants.DRIVING_LESSONS_DB)
                .whereEqualTo("student", student)
                .orderBy("dateTimeInMillis", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects()
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "No se han encontrado las prácticas",
                e.code
            )
        }
    }

    override suspend fun getDrivingLessonsByDrivingSchool(drivingSchoolName: String): List<DrivingLessonModel> {
        try {
            return db.collection(Constants.DRIVING_LESSONS_DB)
                .whereEqualTo("drivingSchool", drivingSchoolName)
                .orderBy("dateTimeInMillis", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects()
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "No se han encontrado las prácticas",
                e.code
            )
        }
    }

    override suspend fun getDrivingLessonById(drivingLessonId: String): DrivingLessonModel? {
        try {
            return db.collection(Constants.DRIVING_LESSONS_DB)
                .document(drivingLessonId)
                .get()
                .await()
                .toObject<DrivingLessonModel>()
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "No se ha encontrado la práctica",
                e.code
            )
        }
    }

    override suspend fun getLastDrivingLessonByStudent(userEmail: String): DrivingLessonModel? {
        try {
            val documents = db.collection(Constants.DRIVING_LESSONS_DB)
                .orderBy("dateTimeInMillis", Query.Direction.DESCENDING)
                .limit(1)
                .whereEqualTo("student", userEmail)
                .get()
                .await()
                .documents
            if (documents.isNotEmpty()) {
                return documents[0].toObject<DrivingLessonModel>()
            }
            return null
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "No se han encontrado prácticas para el usuario",
                e.code
            )
        }
    }

    override suspend fun getLastDrivingLessonByDrivingSchool(drivingSchool: String): DrivingLessonModel? {
        try {
            val documents = db.collection(Constants.DRIVING_LESSONS_DB)
                .orderBy("dateTimeInMillis", Query.Direction.DESCENDING)
                .limit(1)
                .whereEqualTo("drivingSchool", drivingSchool)
                .get()
                .await()
                .documents
            if (documents.isNotEmpty()) {
                return documents[0].toObject<DrivingLessonModel>()
            }
            return null
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "No se han encontrado prácticas para el usuario",
                e.code
            )
        }
    }

    override suspend fun addDrivingLesson(drivingLesson: DrivingLessonModel) {
        try {
            db.collection(Constants.DRIVING_LESSONS_DB).document(drivingLesson.id)
                .set(drivingLesson)
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "Error al añadir práctica a la base de datos",
                e.code
            )
        }
    }

    override suspend fun getUserAchievements(userEmail: String): List<AchievementModel> {
        try {
            return db.collection(Constants.USERS_DB)
                .document(userEmail)
                .collection(Constants.ACHIEVEMENTS_DB)
                .get()
                .await()
                .toObjects()
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "No se han encontrado los logros del usuario",
                e.code
            )
        }
    }

    override suspend fun completeFirstLessonAchievement(userEmail: String) {
        try {
            db.collection(Constants.USERS_DB)
                .document(userEmail)
                .collection(Constants.ACHIEVEMENTS_DB)
                .document(Constants.FIRST_LESSON_ACHIEVEMENT)
                .update(mapOf(
                    "levels.level1.completed" to true
                ))
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "No se ha podido actualizar el logro de primera práctica completada",
                e.code
            )
        }
    }

    override suspend fun completePerfectLessonAchievementLevel(userEmail: String, level: Int) {
        try {
            db.collection(Constants.USERS_DB)
                .document(userEmail)
                .collection(Constants.ACHIEVEMENTS_DB)
                .document(Constants.PERFECT_LESSON_ACHIEVEMENT)
                .update(mapOf(
                    "levels.level${level}.completed" to true
                ))
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(
                "No se ha podido actualizar el logro de práctica sin fallos",
                e.code
            )
        }
    }
}