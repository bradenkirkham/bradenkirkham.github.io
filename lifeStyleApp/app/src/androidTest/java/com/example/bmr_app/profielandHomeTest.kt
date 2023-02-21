package com.example.bmr_app


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class profielandHomeTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACTIVITY_RECOGNITION",
            "android.permission.CAMERA"
        )

    @Test
    fun profielandHomeTest() {
        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.profile_menu), withContentDescription("Profile"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.navigationView),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        val floatingActionButton = onView(
            allOf(
                withId(R.id.photoFlotingbtn),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        2
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.userName),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_host_fragment_container),
                        1
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("test"), closeSoftKeyboard())

        val materialButton = onView(
            allOf(
                withId(R.id.male_toggleBtn), withText("male"),
                childAtPosition(
                    allOf(
                        withId(R.id.genderToggleSwitch),
                        childAtPosition(
                            withId(R.id.linearLayout),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val materialButton2 = onView(
            allOf(
                withId(R.id.female_toggleBtn), withText("female"),
                childAtPosition(
                    allOf(
                        withId(R.id.genderToggleSwitch),
                        childAtPosition(
                            withId(R.id.linearLayout),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialButton2.perform(click())

        val materialButton3 = onView(
            allOf(
                withId(R.id.male_toggleBtn), withText("male"),
                childAtPosition(
                    allOf(
                        withId(R.id.genderToggleSwitch),
                        childAtPosition(
                            withId(R.id.linearLayout),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialButton3.perform(click())

        pressBack()

        val materialButton4 = onView(
            allOf(
                withId(R.id.Done), withText("Done"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_host_fragment_container),
                        1
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        materialButton4.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
