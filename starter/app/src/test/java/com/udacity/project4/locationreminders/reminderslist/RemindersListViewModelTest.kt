package com.udacity.project4.locationreminders.reminderslist

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

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    //executes each task synchronously using architecture component
    @get:Rule
    var instantTaskExecutorRule= InstantTaskExecutorRule()
    //set the main coroutine dispatcher for unit testing
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule= MainCoroutineRule()

    //subject under test
    private lateinit var remindersListViewModel: RemindersListViewModel
    //use a fake repository to be injected into the view model
    private lateinit var remindersDataSource: FakeDataSource

    @Before
    fun setUpRemindersListViewModel(){
        //initialise the repository with no reminders
        remindersDataSource= FakeDataSource()
        remindersListViewModel= RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),remindersDataSource)
    }
    @After
    fun stop(){
        stopKoin()
    }

}