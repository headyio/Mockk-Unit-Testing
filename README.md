# Mockk-Unit-Testing
Mockk Unit Testing by Hithesh Vurjana

This article is not meant to give you exhaustive knowledge of unit-testing. This is meant to be a quick recipe guide for common use cases especially ViewModel testing on Android. Its meant for instant cooking. For a detailed explanation, check the **References** section.

With that said, lets dive into the world of unit testing.

1\. Why write unit-tests?
=========================

https://giphy.com/gifs/baby-sleepy-face-first-xT8qBvH1pAhtfSx52U

Credit: <https://giphy.com/gifs/baby-sleepy-face-first-xT8qBvH1pAhtfSx52U>

I know its not the coolest of android topics but here are some reasons why we write unit tests:

-   Tests ensure that existing functionality remains intact when new changes are introduced.

-   Tests serve as documentation and reflect the code's current behavior.

-   Writing testable code often leads to cleaner, more modular designs.

-   Knowing your code is covered by tests allows you to make changes with confidence.

We have all seen the recent Microsoft outage. Some of you might have even experienced Windows "**Blue Screen of Death**" personally.

The statement given by Crowdstrike, the company responsible for the outage:

> *The outage, which was caused by an undetected error in a rapid response content configuration update to CrowdStrike's Falcon platform, impacted millions of Microsoft computers.*

As per this [tweet](https://x.com/raymo_g/status/1814234785226604963) - <https://x.com/raymo_g/status/1814234785226604963>

> *Around a billion computers are bricked worldwide, mostly corporate ones. This isn't just an online service going down for a few hours. Every affected computer needs to be rebooted in fail mode and have a driver manually removed.*

This is the main reason why we write Unit tests. I hope the developer and the team responsible are still alive. As per [CNN](https://heady.atlassian.net/wiki/pages/resumedraft.action?draftId=2266333227): 

> *The outage may have cost Fortune 500 companies as much as $5.4 billion in revenues and gross profit.*

I hope this is enough reason for you the reader to take testing seriously.

2\. What can we test?
=====================

On Android we can write unit-tests for:

-   **ViewModels**

-   Data layer such as **repositories**. 

-   Domain layer (Platform-independent), such as **use cases**.

-   **Utility classes** such as string, date, math, etc.

For more info check [this](https://developer.android.com/training/testing/fundamentals/what-to-test).

3\. How Android Testing works
=============================

### Organization of test directories

A typical project in Android Studio contains two directories that hold tests depending on their execution environment. Organize your tests in the following directories as described:

-   The androidTest directory should contain the tests that run on real or virtual devices. Such tests include integration tests, end-to-end tests, and other tests where the JVM alone cannot validate your app's functionality.

-   The testdirectory should contain the tests that run on your local machine, such as unit tests. In contrast to the above, these can be tests that run on a local JVM.

### Local tests (test source set)

These tests are run locally on your development machine's JVM and do not require an emulator or physical device. Because of this, they run fast, but their fidelity is lower, meaning they act less like they would in the real world.

### Instrumented tests (androidTest source set)

These tests run on real or emulated Android devices, so they reflect what will happen in the real world, but are also much slower.

### Types of tests

Manual testing is not scalable and cannot cover all the test cases on every change you make. So we use Automated testing which is repeatable, does not require human intervention and are fast. We can categorise tests in many ways but on Android, broadly speaking there are 2 types of tests:

1.  **Instrumented Tests:** These are UI tests which require an Android device to test views. They are slow as they need a device and launch views. They are present in the androidTest source set. These are not part of this article.

2.  **Unit Tests:** Tests that cover a small unit/block of code that does not involve Android UI framework code. These are fast since they don't need external device to test and they run on your local dev machine. They are present in the testsource set. ViewModel testing falls in this category and we will be writing unit tests with Mockk.

4\. How Mocking works
=====================

You have the same three parts in which your tests are divided:

-   Prepare fake objects with fake data.

-   Test the program logic with fake data and objects.

-   Verify if the result matches actual behaviour.

5\. Why mockk?
==============

-   **Kotlin-friendly:** Mockk's syntax is designed to align with Kotlin's idiomatic style, making it easy to write readable and maintainable tests.

-   **Verifying interactions:** Mockk provides a clear and concise syntax for verifying interactions between objects,making it easy to ensure that your code is behaving as expected.

-   **Flexible verification:** Mockk offers a variety of verification methods, including verifying the order of calls, the number of times a method was called, and the arguments passed to a method.

-   **Custom matchers:** You can create custom matchers to verify specific conditions on arguments, making your tests more precise.

-   **Monitor real objects:** Mockk allows you to create spies, which are objects that record their interactions and can also be used to modify the behavior of real objects during testing. As per the documentation spies may not work on JDK 16+.

-   **Test side effects:** Spies are useful for testing methods that have side effects, such as modifying external state or calling other methods.

-   **Test asynchronous code:** Mockk provides built-in support for testing coroutines, making it easy to test asynchronous code in your Android applications.

-   **Verify coroutine behavior:** You can verify the behavior of coroutines using Mockk's verification methods and custom matchers.

-   **Android-friendly:** Mockk integrates seamlessly with Android development tools and libraries, making it easy to use in your unit tests.

-   **Dependency injection:** Mockk can be used with dependency injection frameworks like Dagger or Koin to easily inject mock objects into your test code.

With that said, lets dive into coding. You can find the full code in this Github repository.

<https://github.com/headyio/Mockk-Unit-Testing>

6\. Dependencies
================

testImplementation means these APIs are only accessible in test directories.
```
testImplementation "junit:junit:4.13.2" // Assertions
testImplementation "io.mockk:mockk:1.10.5" // Stubbing/Mocking, Verifying
testImplementation "app.cash.turbine:turbine:0.12.3" // Testing Flows
testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3" // CoroutineRule, TestDispatcherRule
```
7\. Prerequisites 
==================

### **Test runner**

A test runner is a part of JUnit framework that is responsible for executing your test cases. Think of it as the engine that powers your tests. JUnit provides a **default test runner** out of the box. This means you typically don't need to explicitly configure it. When you run your JUnit tests, the default runner is automatically used to discover and execute them.
```
android {
    defaultConfig {
        testInstrumentationRunner 'androidx.test.runner.AndroidJUnitRunner'
    }
}
```
### **@RunWith** Annotation

@RunWith(JUnit4::class) is a JUnit annotation used to specify a different test runner. In this case, it indicates that you want to use the JUnit 4 test runner instead of the default one. Essentially, this annotation tells JUnit to use the JUnit 4 runner for executing your tests, providing a level of compatibility and control over the test execution process.

### Setting Default Values

testOptions { unitTests.returnDefaultValues = true }

In Gradle, this configuration specifies that when a test fails, the test runner should return default values for methods that haven't been executed. This is useful in scenarios where you want to continue testing even if a previous test fails.

### Sample JUnit Assertion

The method compares the expected value with the actual value when you run the test.
```
import org.junit.Assert

Assert.assertEquals(
  /* expected = */ "Hello",
  /* actual = */ "Hello"
)
```
### Sample Turbine Test
```
import app.cash.turbine.test

flowOf("one", "two").test {
  assertEquals("one", awaitItem())
  assertEquals("two", awaitItem())
  awaitComplete()
}
```
### Sample Coroutine Test

As per documentation, TestWatcher class can have Rules that keep track of testing action without modifying the action. Like maintain a log of failed and passed tests.

To test coroutines we need a JUnit 4 TestRule which creates a new CoroutineScope for each test, sets Dispatchers.Main, and cleans up Coroutines afterwards.

A TestDispatcher is used to control the execution of coroutines within a test environment. We can set StandardTestDispatcher() for complex operations and UnconfinedTestDispatcher() for simpler operations.
```
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class CoroutineRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}`
```
### @get:Rule

@get:Ruleannotation is used to inject a field into a test class**.** This field becomes a rule that is automatically managed by JUnit during test execution. val testName = TestName() in this context means that the testName field is declared as a rule, and it's initialized with an instance of the TestName class. The TestName class provides access to the current test's name. This is useful for dynamic test generation or logging purposes.
```
@get:Rule
val coroutineRule = CoroutineRule()

@get:Rule
val testName = TestName()

@get:Rule
val testDispatcherRule = TestDispatcherRule()

@Before
fun setUp() {
    MockKAnnotations.init(this)

    when (testName.methodName) {
        "Check api call is successful" -> {
            // Stub for success scenario
        }

        "Check api call failed" -> {
            // Stub for failure scenario
        }
    }

    viewModel = ExploreViewModel()
}

@Test
fun `Check api call is successful`() = runTest { }

@Test
fun `Check api call failed`() = runTest { }
```
8\. Mockk basics
================

### How to create a fake object a.k.a mock or stub?

You get io.mockk.MockKException: no answer found if the stub is either incorrect or if the parameters do not match or if you forgot to create a stub. Safest option is to first add all parameters to any() and try to pass the test.
```
# Non suspend function stub
every { function() } returns { Object or Unit }

# Suspend function stub
coEvery { function() } coAnswers { Object or Unit }`
```
### Testing Static Stuff

UsingmockkStatic(Obj::class) in Kotlin is a function from the MockK library used to mock static methods of a class.
```
@Test
fun testStaticMethod() {
    mockkStatic(MyClass::class)

    every { MyClass.staticMethod() } returns "mocked value"

    val result = MyClass.staticMethod()
    assertEquals("mocked value", result)
}
```
### Testing API calls

-   @Test: This marks the function as a test case to be executed by a testing framework like JUnit.

-   viewModel.viewState.test { ... }: This line tests the viewState property of a viewModel object. The test block is used to interact with the property and observe its changes.

-   result: This variable stores the current value of the viewState property.
```
@Test
fun `Check api call is successful and gives list`() = runTest {
    viewModel.viewState.test {
        val result = awaitItem()
        val expected = VehicleViewState(engineList = engineList)
        Assert.assertEquals(expected, result)
        coVerify(exactly = 1) {
            vehicleRepository.getVehicleData(1, "Random")
        }
    }
}`
```
### Testing Input field Validations

This test verifies that the viewmodel's password validation logic correctly identifies valid and invalid passwords based on the given criteria. The test uses asynchronous testing and simulates time passage to ensure the validation logic works as expected under different conditions.
```
@Test
fun `Check password input is valid or not`() = runTest {
    viewModel.password.test {
        println(awaitItem())

        advanceTimeBy(DEBOUNCE_TIME_OUT)
        viewModel.setPassword("Test@12")
        passwordValidationState = awaitItem()
        assertEquals(false, passwordValidationState.second)

        advanceTimeBy(DEBOUNCE_TIME_OUT)
        viewModel.setPassword("Test@123")
        passwordValidationState = awaitItem()
        assertEquals(true, passwordValidationState.second)

        advanceTimeBy(DEBOUNCE_TIME_OUT)
        viewModel.setPassword("")
        passwordValidationState = awaitItem()
        assertEquals(false, passwordValidationState.second)
    }
}
```
9\. Common Errors
=================

-   You will get No value produced in 3s app.cash.turbine.TurbineAssertionError error if you skip all emissions.

-   If you do not skip emissions and do not assert, then you get Unconsumed events found. This is good, because it shows in the error the details of the emission.

10\. Conclusion
===============

Mockk is an easy to use, Kotlin friendly mocking framework which enables you to write unit-tests effortlessly. After reading this article, you should have the ability to mock objects and write unit tests with ease. Thats all. With this I think your app is ready for battle.

https://giphy.com/gifs/marvelstudios-dv01JuAyGK11zZKRv5

Credit: <https://giphy.com/gifs/marvelstudios-dv01JuAyGK11zZKRv5>

References
==========

1.  [https://mockk.io](https://mockk.io/)

2.  <https://github.com/cashapp/turbine>

3.  <https://developer.android.com/kotlin/flow/test>

4.  <https://developer.android.com/training/testing/local-tests>
