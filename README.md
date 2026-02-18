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
<br/>
<img width="1895" height="1393" alt="image" src="https://github.com/user-attachments/assets/a0400ec7-8ccd-4cc4-83f7-adc930ac2e31" />
<br/>
<br/>
<img width="1856" height="1398" alt="image" src="https://github.com/user-attachments/assets/27791251-75e8-4310-aba6-1bec2f6f8191" />
<br/>
<br/>
<br/>
<br/>
Signing-up requires legal email address, a password and chosen nickname (can be written in either English or Hebrew)
A toggle button allows a user to pick between signing up as a student or as a teacher.
<br/>
<br/>
<br/>




After a successful sign-up, the teacher will be stored in the app's database, which is a firebase real-time database. Thus, usage of the app requires internet connection.
The user's details will be stored in the app, and enable automatic log-in, until the user will decide to log-out.




A teacher can create subjects (such as math, physics, etc) and add tasks and classrooms to each subject.
<br/>
<br/>
<br/>
<img width="1890" height="1399" alt="image" src="https://github.com/user-attachments/assets/614e6497-fc99-44e6-8ddd-ec8da4d1a1e8" />

<br/>
<br/>
<img width="1878" height="1400" alt="image" src="https://github.com/user-attachments/assets/d4f93d64-312f-44ec-8abd-15e7563765aa" />

<br/>
<br/>
<br/>
<br/>

A task is an object with string description, which allows the teacher to keep track of which tasks groups can get.

<br/>
<br/>
<br/>
<img width="1730" height="1407" alt="image" src="https://github.com/user-attachments/assets/5c048878-f70e-4143-8dfd-c53110c34f2a" />
<br/>
<br/>
<img width="1724" height="1396" alt="image" src="https://github.com/user-attachments/assets/bba9b68b-5f5f-4a53-922a-49a2836fe086" />

<br/>
<br/>
<br/>
<br/>


A classroom contains name and a list of student.

<br/>
<br/>
<br/>
<img width="1739" height="1407" alt="image" src="https://github.com/user-attachments/assets/a998ced1-d26a-45b1-969c-dd898294e16c" />
<br/>
<br/>
<br/>
<br/>


In order to add students, the teacher needs to create a classroom code. Students can enter the code to their own app and be stored in the classroom. Each student that signs to the classroom shows up in the teacher's classroom screen in the nickname they chose for themeselves.
(more on the student's side later)

<br/>
<br/>
<br/>
<img width="1738" height="1392" alt="image" src="https://github.com/user-attachments/assets/faec8d09-6e7f-4daa-9668-aa06e5b5db63" />

<br/>
<br/>
<img width="1822" height="1544" alt="image" src="https://github.com/user-attachments/assets/5d25ed6d-3b8e-44cf-bf7a-2902550e1e31" />

<br/>
<br/>
<br/>
<br/>

When the theacher wants to create a group, they can do so using the '+', and customize the group variables as stated before.

<br/>
<br/>
<br/>

<img width="2032" height="1404" alt="image" src="https://github.com/user-attachments/assets/3b25951a-6338-438e-9d0f-b3c64e19548a" />

<br/>
<br/>

<img width="2039" height="1408" alt="image" src="https://github.com/user-attachments/assets/fe7a3a9a-3264-4c21-9fab-311f9b1ec061" />


<br/>
<br/>
<br/>
<br/>

After pressing 'OK', the groups will be automatically visible in the student's screens.
When pressing 'delete groups', each group will be deleted and thus a new set of randomized groups can be created again.

<br/>
<br/>
<br/>
<img width="2025" height="1407" alt="image" src="https://github.com/user-attachments/assets/a41fc492-dc7c-4163-8257-28dddc466615" />



<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>




**Student-side:**
A student signs up to the app in the same way as a teacher, exept for toggling to 'student' in the sign-up toggle button.

<br/>
<br/>
<br/>

<img width="2040" height="1393" alt="image" src="https://github.com/user-attachments/assets/d00334e0-175c-4461-95bb-334c16556a83" />

<br/>
<br/>
<br/>
<br/>

After the student logs in, they will see the classrooms they signed into (using a code)
A new classroom can be added using the '+' button and typing the code given by the classroom's teacher.

<br/>
<br/>
<br/>

<img width="2019" height="1395" alt="image" src="https://github.com/user-attachments/assets/287f28d3-1dcb-4238-b35f-f1d166e76cdb" />

<br/>
<br/>

<img width="2039" height="1409" alt="image" src="https://github.com/user-attachments/assets/ec769985-e7e1-4c11-a41b-591deb29dc53" />

<br/>
<br/>
<br/>
<br/>

When no groups exist, this is the default group screen each student has;

<br/>
<br/>
<br/>

<img width="2037" height="1403" alt="image" src="https://github.com/user-attachments/assets/7c274752-166e-4d73-9057-e7722b34604d" />

<br/>
<br/>
<br/>
<br/>

And here is an example of how the group screen looks when groups were created;

<br/>
<br/>
<br/>

<img width="2035" height="1405" alt="image" src="https://github.com/user-attachments/assets/a932645d-76d7-493b-b784-532f4c9360b7" />

<br/>
<br/>
<br/>
<br/>


An example of how groups screens of 4 different students look like, in the default case, where each group has unique number, color and symbol;

<br/>
<br/>
<br/>

<img width="2608" height="1565" alt="image" src="https://github.com/user-attachments/assets/530e287e-ee2a-4883-8e2e-ac9d36b583cc" />
<br/>
<br/>
<br/>
<br/>

An example of how groups screens of 4 different students look like, in the case where the color variable of a group is independent to number and color.
Aka, each student has 2 groups: a group denoted by number and symbol, and another group denoted by color:

<br/>
<br/>
<br/>
<img width="2605" height="1570" alt="image" src="https://github.com/user-attachments/assets/2023bd42-3efe-426d-a886-d36b7af5f025" />
<br/>
<br/>
<br/>
<br/>


Here is a similar example, but in the case where symbols are independent of number and color;

<br/>
<br/>
<br/>
<img width="2594" height="1576" alt="image" src="https://github.com/user-attachments/assets/eae49f55-01a2-405a-acc5-f0b57663f199" />

<br/>
<br/>
<br/>
<br/>


An example of the last case, where number, color and symbol are independent.
Aka, each student has theree groups, one denoted by number, other by color and another by symbol;

<br/>
<br/>
<br/>

![WhatsApp Image 2026-02-18 at 14 01 48](https://github.com/user-attachments/assets/91d7dfac-3a4d-4820-bea5-a8bc635db83d)


