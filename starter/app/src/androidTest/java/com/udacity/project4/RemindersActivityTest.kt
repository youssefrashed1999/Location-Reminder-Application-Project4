package com.udacity.project4

import android.app.Activity
import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource= DataBindingIdlingResource()
    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }
    @Before
    fun registerIdlingResources(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }
    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResources(){
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }
    //    TODO: add End to End testing to the app
    @Test
    fun saveReminderWithNoData_ShowTitleError(){
        //start Reminders Activity
        val activityScenario= ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        //click of add new reminder button
        onView(withId(R.id.addReminderFAB)).perform(click())
        //click on the save button without adding any data
        onView(withId(R.id.saveReminder)).perform(click())
        //The error message should be a title error message
        onView(withText(R.string.err_enter_title)).check(matches(isDisplayed()))
        //close the scenario
        activityScenario.close()
    }
    @Test
    fun saveReminderWithNoLocation_ShowLocationError(){
        //start Reminders Activity
        val activityScenario= ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        //click of add new reminder button
        onView(withId(R.id.addReminderFAB)).perform(click())
        //Add title
        onView(withId(R.id.reminderTitle)).perform(typeText("New Title"))
        //Add description
        onView(withId(R.id.reminderDescription)).perform(typeText("New Description"))
        //close the keyboard
        Espresso.closeSoftKeyboard()
        //click on save button without adding location
        onView(withId(R.id.saveReminder)).perform(click())
        //The error message should be a location error message
        onView(withText(R.string.err_select_location)).check(matches(isDisplayed()))
        //close the scenario
        activityScenario.close()
    }
    @Test
    fun saveReminderWithAllDetails_ShowSuccess(){
        //start Reminders Activity
        val activityScenario= ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        //click of add new reminder button
        onView(withId(R.id.addReminderFAB)).perform(click())
        //Add title
        onView(withId(R.id.reminderTitle)).perform(typeText("New Title"))
        //Add description
        onView(withId(R.id.reminderDescription)).perform(typeText("New Description"))
        //close the keyboard
        Espresso.closeSoftKeyboard()
        //Click on select location button
        onView(withId(R.id.selectLocation)).perform(click())
        //click a long click on the map to get a location
        onView((withId(R.id.map_fragment))).perform(longClick())
        //click save to save the location
        onView((withId(R.id.saveLocation))).perform(click())
        //click on save button to save reminder
        onView(withId(R.id.saveReminder)).perform(click())
        //check for the toast
        onView(withText(R.string.reminder_saved))
            .inRoot(RootMatchers.withDecorView(not(`is`(getActivity(activityScenario).window.decorView))))
            .check(matches(isDisplayed()))
        //close the scenario
        activityScenario.close()
    }
    @Test
    fun saveReminderWithNoDescription_ShowSuccess(){
        //start Reminders Activity
        val activityScenario= ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        //click of add new reminder button
        onView(withId(R.id.addReminderFAB)).perform(click())
        //Add title
        onView(withId(R.id.reminderTitle)).perform(typeText("New Title"))
        //close the keyboard
        Espresso.closeSoftKeyboard()
        //Click on select location button
        onView(withId(R.id.selectLocation)).perform(click())
        //click a long click on the map to get a location
        onView((withId(R.id.map_fragment))).perform(longClick())
        //click save to save the location
        onView((withId(R.id.saveLocation))).perform(click())
        //click on save button to save reminder
        onView(withId(R.id.saveReminder)).perform(click())
        //check for the toast
        onView(withText(R.string.reminder_saved))
            .inRoot(RootMatchers.withDecorView(not(`is`(getActivity(activityScenario).window.decorView))))
            .check(matches(isDisplayed()))
        //close the scenario
        activityScenario.close()
    }
    private fun getActivity(activityScenario: ActivityScenario<RemindersActivity>): Activity {
        lateinit var activity: Activity
        activityScenario.onActivity {
            activity=it
        }
        return activity
    }

}
