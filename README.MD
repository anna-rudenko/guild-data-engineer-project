# How To Run

1. Start up docker and create movie DB inside of it
    - from the main directory - **guild-data-engineer-project**
    - `docker-compose up -d`
2. Install project using maven
    - `mvn clean install`
3. Run the shell script, providing S3 endpoint as argument
    - `./run.sh "s3://com.guild.us-west-2.public-data/project-data/the-movies-dataset.zip"`
4. Output files will end up in **guild-data-engineer-project/working_directory/movie_result**s
5. Logs will end up in **guild-data-engineer-project/log**
    - Exact output files are stated in the main log - **data-engineer-project.log**
6. After project has finished running, shut down docker
    - `docker-compose down`

# ERD Diagram

![erd diagram](diagrams/movies-erd.png)

# Modeling Decision

1. The given data fits into a relational database with minimal cleanup.
    1. There are three main data entities - a movie, a genre, and a production company.
    2. To avoid many-to-many relationships, a table is used to tie a genre and a production company
       to a movie.
2. Tech Stack
    1. **Postgres** - Postgres is a free relational database, but it can also be very powerful.
        1. It can handle very large tables if partitioning is added.
    2. **Java** - I went with Java because I am most familiar with it, and because it is powerful
       and capable of handling large amounts of data.
    3. **Maven** - I went with the maven build tool, as that's the one I'm familiar with.
    4. **Docker** - The database for this project runs inside a docker container, so that it doesn't
       mess up anyone's local install.

# Future Needs

## 100x Increase

Solutions for a 100x increase in data volume, and an hourly update cadence:

1. We can continue using the Postgres DB for movie data, since it isn't too complex.
2. Additionally, Postgres is capable is handling larger amounts of data, but partitioning needs to
   be added to the tables.
    2. A good way of partitioning tables would be to add a timestamp column and using it. With a
       known hourly cadence, we expect about 24 files a day - we can choose to create a new
       partition each week.
    3. For the sake of this exercise, I create a new, clean DB each time. This will not work in the
       new scenario. Instead, a more permanent database should be set up.
2. Java code - my code right now is quite simple and works for the exercise. To scale it:
    1. Break the application down into parts - a reader, a worker, and a writer part
        1. As each part processes records, it will put them onto a queue, then the next part can
           pick it up
    2. Add multithreading to this application to speed up processing
3. Overall process - we would set up a workflow in a tool such as airflow to automatically run
   hourly as new files come in.

## Data Reprocessing

To backfill 1 year worth of data, avoiding impact on current production flow we could break up the
older data into smaller files - equivalent in size to the hourly file we get in a normal data flow.
In a multi-treaded application, even at 100x the total data in this project, an hourly file would
only take minutes. We can create a second automated workflow to run files that need to be
back-filled on the half-hour, until all of the data is back-filled.

## Error Handling

There are several considerations when it comes to error-handling:

1. Validating the incoming data to make sure bad data doesn't cause errors
    1. Create a validator util that will apply rules to each piece of data
2. Handling a process breakdown
    1. The airflow process should send an email (or another alert type) when the hourly process
       doesn't finish without error - whether there is an authentication failure or another issue.
    2. Manual intervention would be needed at that point to correct the issue, and kick the job off
       again.
