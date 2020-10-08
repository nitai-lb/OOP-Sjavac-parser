# OOP-Sjavac-parser
This repo is an Sjavac parser where Sjavac stands for simple java code. In this code I used 2-way scan, regex and some oop design patterns.

**FILE DESCRIPTION**
 The following packages were created:
 main: Contains all of the classes which handle reading and parsing the code, while also containing the main running
 class Sjavc and both factories.
 Block: Contains all of the classes which are used to create block objects, block objects are the types that
 create new scopes.
 Variables: Contains all of the classes which are used to create variables and have a few methods which
 check the validity of those variables.
 Exceptions: contains all exceptions that Sjavac may throw.
___________________________________________________________
Brief explanation of the main methods within each package:

 Main:
 first/secondScan():
 both these functions read through the script and deduce if the script is legal.
 During the first scan, the only scope actually read in depth is the global scope. Although all lines are read
 just to make sure all lines are legal lines, only global variables and methods are created.
 This was a huge part of our design, because making sure the global scope is first valid made it very simple to check
 if the inner scopes are legally made.
 In respect to the secondScan, as previously mentioned, the secondScan focuses on the inner scopes, with both
 inner scope variable creation and validity while also creating the new scopes of If/While.

 Block:
 Global creation and reset:
 these methods are unique to Global as we made Global a singleton class. Knowing a global scope is always existent, but
 also unique as it is a one of a kind, we found that making the Global object as a singleton object quite natural.
 With every text that needs dissecting we create a global object, and with each ending of a code we use the "finally"
 function to reset the global object for the next test.

 Variables:
 This package creates Variables of different types. Using the Variable class as an abstract object it lays a template
 for all other Variables. The Variable object is created with booleans such as "is it final" or "is it assigned" to
 help us keep track of whether or not the variable can do certain things.

 Exceptions:
 All exceptions in this exercise were made to be able to be caught and print out "1" which is why they do not contain
 any information or messages.
