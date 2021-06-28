# World Clock Application

An Android Application which shows updated times of 424 cities of the World. The user can select cities as favorite to track their current times.

# User Interface

The android application has two main user interfaces:

- In the first interface, the current time of the selected user cities is shown.

<p align="center">
    <img src="/Resources/first-activity.jpg" width="400" height="600">
</p>

- In the second interface, all the available cities and their current times are shown. The information is fetched using an API.

<p align="center">
    <img src="/Resources/second-activity.jpg" width="400" height="600">
</p>

# Implementation Details

- The current time of the cities is fetched using the <a href="https://timezonedb.com/references/list-time-zone">Timezonedb</a> API. It is free-to-use. However, you need to create an account to get the API key.

- The current times and country codes are fetched using the above API in a started service. The data is then stored in the SQLite database.

* User selects his/her favorite cities from the second interface and sends them to the first one for tracking.

* The times of the selected cities in the first user interface are updated using a background thread after reach second. It ensures that the main UI thread does not get blocked.

# Tools & Technologies Used

- Android Studio
- SQLite Database
- Timezonedb API
- Java

# How to Run?

- To run the code, clone the repository and open the project in Android Studio.

# To-do

- Improve UI
- Use a JobSchedular Service.
- Use Content Provider