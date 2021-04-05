# rafko.aidertool
Java utility program to request help and react to help requests. Aims to minimise the hassle that comes with asking for code review requests and asking for one-on-one help. 

The tool consists of an agent and a dealer app. The Dealer is the one storing the help requests, while agents interact with one another through the dealer. The requests can have the following states: 

 - open(green): A help request(and its requester) is awaiting a helper
 - snoozed(gray): A help request is noted- but de-prioritized by the helper
 - active(yellow): A helper accepted to help the requester of an active help request. 
 - pending(orange): Either the helper or the requester suggests that the interaction bore fruit, and is ready to be closed
 - finished, cancelled(no color): the request is being removed from the system

---
 
Current Framework does not include communication channels, other than the above described; It aims to only supplement any toolchain a development team might have.