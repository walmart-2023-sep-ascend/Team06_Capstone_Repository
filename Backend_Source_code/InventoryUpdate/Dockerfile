FROM openjdk:17
EXPOSE 9502
ADD target/inventory-update-service.jar inventory-update-service.jar
ENTRYPOINT ["java","-jar","inventory-update-service.jar"]