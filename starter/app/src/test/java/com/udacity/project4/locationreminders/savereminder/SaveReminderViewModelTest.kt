package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk=[28])
class SaveReminderViewModelTest {
    //TODO: provide testing to the SaveReminderView and its live data objects
    //execute each task synchronously using architecture component
    @get:Rule
    var instantTaskExecutorRule= InstantTaskExecutorRule()
    //set the main coroutine dispatcher for unit testing
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule= MainCoroutineRule()
    //subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    //use a fake repository to be injected in the view model
    private lateinit var reminderDataSource: FakeDataSource
    @Before
    fun setUpSaveReminderViewModel(){
        //initialise the repository with no reminders
        reminderDataSource= FakeDataSource()
        saveReminderViewModel=SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),reminderDataSource)
    }
    @After
    fun stop(){
        stopKoin()
    }

}