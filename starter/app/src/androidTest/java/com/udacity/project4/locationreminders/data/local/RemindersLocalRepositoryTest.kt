package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
//    TODO: Add testing implementation to the RemindersLocalRepository.kt
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var localRepository: RemindersLocalRepository
    private lateinit var db: RemindersDatabase
    @Before
    fun setup(){
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        db=Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        localRepository=RemindersLocalRepository(db.reminderDao(),Dispatchers.Main)
    }
    @After
    fun close()=db.close()
    @Test
    fun saveReminder_getReminder()= runBlocking {
        //GIVEN - a new reminder saved in the database
        val reminder=ReminderDTO("Youssef","Mohamed","Rashed",1.0,1.0)
        localRepository.saveReminder(reminder)
        //WHEN - reminder is retrieved by id
        val result=localRepository.getReminder(reminder.id)
        //THEN - same reminder is returned
        assertThat(result.succeeded,`is`(true))
        result as Result.Success
        assertThat(result.data.id,`is`(reminder.id))
        assertThat(result.data.title,`is`(reminder.title))
        assertThat(result.data.description,`is`(reminder.description))
        assertThat(result.data.location,`is`(reminder.location))
        assertThat(result.data.longitude,`is`(reminder.longitude))
        assertThat(result.data.latitude,`is`(reminder.latitude))
    }
    @Test
    fun saveReminders_deleteAllReminders()= runBlocking {
        //GIVEN - 2 new reminders saved in the database
        val reminder1=ReminderDTO("Youssef","Mohamed","Rashed",1.0,1.0)
        val reminder2=ReminderDTO("Mohamed","Ahmed","Mabrouk",2.0,2.0)
        localRepository.saveReminder(reminder1)
        localRepository.saveReminder(reminder2)
        //WHEN - reminders are deleted then retrieved
        localRepository.deleteAllReminders()
        val result=localRepository.getReminders()
        //THEN - retrieved list is empty
        assertThat(result.succeeded,`is`(true))
        result as Result.Success
        assertThat(result.data.isEmpty(),`is`(true))
    }
    @Test
    fun saveReminders_getReminders()= runBlocking {
        //GIVEN - 2 new reminders saved in the database
        val reminder1=ReminderDTO("Youssef","Mohamed","Rashed",1.0,1.0)
        val reminder2=ReminderDTO("Mohamed","Ahmed","Mabrouk",2.0,2.0)
        localRepository.saveReminder(reminder1)
        localRepository.saveReminder(reminder2)
        //WHEN - reminders are retrieved
        val result=localRepository.getReminders()
        //THEN - list with the two reminders are retrieved
        assertThat(result.succeeded,`is`(true))
        result as Result.Success
        assertThat(result.data.size,`is`(2))
    }
    @Test
    fun saveReminder_getReminderNotInDatabase()= runBlocking {
        //GIVEN - a new reminder saved in the database
        val reminder=ReminderDTO("Youssef","Mohamed","Rashed",1.0,1.0)
        localRepository.saveReminder(reminder)
        //WHEN - get reminder with id not saved in the database
        val result=localRepository.getReminder("WSXR")
        //THEN -error occurred
        assertThat(result.error,`is`(true))
    }
}