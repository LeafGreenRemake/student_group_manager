Student Manager Group - an application to create randomized student groups.
<br/>
<br/>
<br/>

**High-level explanation of the app:**
<br/>
<br/>
The app allows a teacher to store students by classrooms, and create randomized groups of these students.
The groups are visible to the students immediatelly via their phones (examples in the screenshots below)
<br/>

Each group has three variables; number, color and symbol.
The teacher can control which of these variables are independent.
Aka, in the default case, each group has unique color, number and symbol.
If, however, the teacher wants to create two sets of groups; by number and by color, then each student belongs to two groups, one denoted by number and the other by color.
<br/>

Below is an explanation of how teachers and students can use the app.
<br/>
<br/>
<br/>


**Teacher-side:**
A teacher, upon first use of the app, needs to sign-up.
<br/>
<br/>
<img width="1856" height="1398" alt="image" src="https://github.com/user-attachments/assets/27791251-75e8-4310-aba6-1bec2f6f8191" />
<br/>
<br/>
Signing-up requires legal email address, a password and chosen nickname (can be written in either English or Hebrew)
A toggle button allows a user to pick between signing up as a student or as a teacher.
<br/>
<br/>
<img width="668" height="1409" alt="image" src="https://github.com/user-attachments/assets/b436a4bf-30ae-4a38-b6c1-31b21b73f561" />
<br/>
<br/>




After a successful sign-up, the teacher will be stored in the app's database, which is a firebase real-time database. Thus, usage of the app requires internet connection.
The user's details will be stored in the app, and enable automatic log-in, until the user will decide to log-out.
<br/>
<img width="1858" height="1389" alt="image" src="https://github.com/user-attachments/assets/4ee39b33-e525-49a2-9340-0bfa2a94c616" />


<br/>


A teacher can create subjects (such as math, physics, etc) and add tasks and classrooms to each subject.
A task is an object with string description, which allows the teacher to keep track of which tasks groups can get.

A classroom contains name and a list of student.
In order to add students, the teacher needs to create a classroom code. Students can enter the code to their own app and be stored in the classroom. Each student that signs to the classroom shows up in the teacher's classroom screen in the nickname they chose for themeselves.



<br/>
<br/>
<br/>


**Student-side:**
