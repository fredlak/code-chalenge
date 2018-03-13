
#Code challenge app: 

##Build application: 

mvn clean install

##Start application:
cd target
java -jar challenge-jar-with-dependencies.jar

##API description:

##To add new message for user:
http://localhost:8080/add-message?user=[user_name]&message=[new message content]
Remarks: if user doesn't exists then above line will add new user

##To show messages of existing user:
http://localhost:8080/show-messages?user=[user_name]


##To show messages of existing user in reverse order:
http://localhost:8080/show-messages?user=[user_name]&reverse-order


##To add follow user to other user:
http://localhost:8080/add-follow-user?user=[user_name]&follow-user=[follow_user_name]
Remarks: users must exists


##To show all messages from all "follow-users"
http://localhost:8080/show-follow-users-messages?user=[user_name]


##To show all messages from all "follow-users" in reverse order
http://localhost:8080/show-follow-users-messages?user=[user_name]&reverse-order
