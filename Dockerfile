# שלב ראשון: Build
FROM maven:3.9.5-eclipse-temurin-17 AS build

# הגדרת תיקיית העבודה
WORKDIR /app

# העתקת קבצי הפרויקט
COPY pom.xml .
COPY src ./src

# בניית ה-JAR
RUN mvn clean package -DskipTests

# שלב שני: Runtime
FROM eclipse-temurin:17-jre

# הגדרת תיקיית העבודה
WORKDIR /app

# העתקת ה-JAR שנבנה בשלב הקודם
COPY --from=build /app/target/*.jar app.jar

# פתיחת הפורט (הפורט שבו Spring Boot רץ)
EXPOSE 9124

# פקודת ההרצה של האפליקציה
ENTRYPOINT ["java", "-jar", "app.jar"]
