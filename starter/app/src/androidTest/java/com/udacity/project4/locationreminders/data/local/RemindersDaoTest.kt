package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
//    TODO: Add testing implementation to the RemindersDao.kt
    //exectues each task synchronously using architecture component
    @get:Rule
    var instantTaskExecutorRule=InstantTaskExecutorRule()
    private lateinit var db:RemindersDatabase
    @Before
    fun initDb(){
        db=Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java).build()
    }
    @After
    fun closeDb() = db.close()
    @Test
    fun insertReminderAndGetByID()= runBlockingTest {
        //GIVEN - insert a reminder
        val reminder=ReminderDTO("Youssef","Mohamed","Rashed",1.0,2.0)
        db.reminderDao().saveReminder(reminder)
        //WHEN - get the reminders from the database
        val loaded=db.reminderDao().getReminderById(reminder.id)
        //THEN - the loaded data contains the expected values
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id,`is`(reminder.id))
        assertThat(loaded.title,`is`(reminder.title))
        assertThat(loaded.description,`is`(reminder.description))
        assertThat(loaded.location,`is`(reminder.location))
        assertThat(loaded.longitude,`is`(reminder.longitude))
        assertThat(loaded.latitude,`is`(reminder.latitude))
    }
    @Test
    fun insertRemindersAndGetThemBack()= runBlockingTest {
        //GIVEN - insert 2 reminders
        val reminder1=ReminderDTO("Youssef","Mohamed","Rashed",1.0,1.0)
        val reminder2=ReminderDTO("Mohamed","Ahmed","Mabrouk",2.0,2.0)
        db.reminderDao().saveReminder(reminder1)
        db.reminderDao().saveReminder(reminder2)
        //WHEN - get the reminders from the database
        val loadedlist=db.reminderDao().getReminders()
        //THEN - the loaded list contains 2 reminders
        assertThat(loadedlist.size,`is`(2))
    }
    @Test
    fun insertRemindersAndDeleteThem()= runBlockingTest {
        //GIVEN - insert 2 reminders
        val reminder1=ReminderDTO("Youssef","Mohamed","Rashed",1.0,1.0)
        val reminder2=ReminderDTO("Mohamed","Ahmed","Mabrouk",2.0,2.0)
        db.reminderDao().saveReminder(reminder1)
        db.reminderDao().saveReminder(reminder2)
        //WHEN - delete the reminders from the database and get them
        db.reminderDao().deleteAllReminders()
        val loadedlist=db.reminderDao().getReminders()
        //THEN - the loaded list is empty
        assertThat(loadedlist.isEmpty(),`is`(true))
    }
}