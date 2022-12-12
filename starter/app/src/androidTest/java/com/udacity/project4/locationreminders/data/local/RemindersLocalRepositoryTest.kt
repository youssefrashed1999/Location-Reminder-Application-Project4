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
}