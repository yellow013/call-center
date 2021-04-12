# Call Center
Imagine you have a call center with three levels of employees: fresher, technical lead (TL), product manager (PM). There can be multiple employees, but only one TL or PM. An incoming telephone call must be allocated to a fresher who is free. If no freshers are free, or if the current fresher is unable to solve the caller's problem (determined by a simple dice roll), he or she must escalate the call to technical lead. If the TL is not free or not able to handle it, then the call should be escalated to the PM.
 
What we would like to see:
1. Create an object-oriented design for this problem. Flexible design which can be extend is preferred.
2. Do this with multi-threading and simple dice roll function
3. Compliable runnable and testable code.
4. Design a high-concurrency call center and draw a system architecture diagram
