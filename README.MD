# Android Application Example - Github Search

Simple Android application build during a job application process.

Provides an input field for a search term. This term is used to search for Github Repos using the
Github API. Results are presented in a list. Buttons at the bottom can be used to navigate the list
as only 10 results are shown per page. Clicking a result in the list opens the corresponding web
page for the selected repository in the system browser.

Some code in the "SearchGithubReposTask.kt" class can be modified to display a detail view when
selecting a result in the list. Clicking on the URL presented in that detail view again opens the
corresponding web page in the system browser. I have disabled the detail view per default because
there needs to be done some work when returning from the detail view to the result view (i.e.
preserve the state when leaving the result view and restore it on return).

This is the first project I have written in Kotlin (with a lot of help from the internet :)).