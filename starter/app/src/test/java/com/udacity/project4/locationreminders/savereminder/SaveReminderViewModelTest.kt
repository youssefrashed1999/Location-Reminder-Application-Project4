package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
    @Test
    fun saveReminder_showLoading(){
        //dummy reminder
        val reminder= ReminderDataItem("x","x","x",0.0,0.0)
        //pause dispatcher to verify values
        mainCoroutineRule.pauseDispatcher()
        //add the dummy reminder
        saveReminderViewModel.saveReminder(reminder)
        //assert that loading indicator is shown
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(),`is`(true))
        //execute pending coroutine functions
        mainCoroutineRule.resumeDispatcher()
        //assert that loading indicator is gone
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }
    @Test
    fun saveReminderWithMissingTitle_ErrorShown(){
        //dummy reminder
        val reminder=ReminderDataItem("","x","x",0.0,0.0)
        //assert that the validation function returns false
        assertThat(saveReminderViewModel.validateEnteredData(reminder),`is`(false))
        //assert that the error message is shown
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),`is`(R.string.err_enter_title))
    }

}