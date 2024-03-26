# Authorization model overview
**Authentication** in this application is provided by an external system, such as EU Login.

**Authorization** is implemented following **attribute-based access control (ABAC)** model, also known as policy-based access control (PBAC).

## What is attribute-based access control?
In ABAC, access rights are granted to users through the use of policies which combine attributes.
Policies can be granting or denying. 

## Why we choose ABAC?
Choosing the most appropriate authorization model and correctly implementing it in a corporate system is a nontrivial task.

There are many authorization models but going through them is out of the scope of this document. 
One that has been around most of us is **Role-based access control (RBAC)**, based on roles and privileges. 
This model is not good enough for our application because it brings problems such as role explosion, manageability, and lack of granularity. 
Which impacts on scalability and maintainability.

Another model that addresses those problems is **access-control list (ACL)** AKA Object Based Access Control, that specifies which users are granted access to each domain object. 
We did consider this model but after discussion with the stakeholders it has been decided that it is too granular for us. 
It would make sense for a forum application where users want control over other users' rights on single posts. 
But in our case it is not expected that a user will  want access control over a single message.
So ACL is too cumbersome for our needs.

ABAC does not suffer from the RBAC scalability and maintainability issues. It is granular enough for our requirements and simpler than ACL. 
Actually, ABAC is flexible on that front, It can be fine or coarse grained.  
Policies can be easily modified and set for given periods of time. 
Groups and users and permissions can be added, removed, granted or revoked easily at database level. No need for deployments like in RBAC.

## How does one of our policies look like?
One of our policies contains the following attributes;
- **domain type**, (Eg. eu.europa.ec.etrustex.web.persistence.entity.Message),
- **role**, (Eg. ADMIN or USER),
- **action**, (One of CREATE, RETRIEVE, UPDATE, DELETE)
- **description**, (Eg. Allow user create a new message)

## How do we use them?
Each request mapping is protected by a permission evaluator that verifies if a granting policy exists for the current context (role, group, action and domain type).

In practice for the world outside of the implementation, a policy is just something like "Allow members of group <A_GROUP> with role <A_ROLE> to <PERFORM_ACTION> on <DOMAIN_ENTITY>"


Example:

Allow members of group EDMA with role ADMIN to CREATE GROUP


## Update 10/02/2021. ETRUSTEX-6555 reduce Policies verbosity
From previous and current analysis, it is obvious that the chosen access control model, Attribute-Based Access Control (ABAC) is the most appropriate for etrustex-web application.
However, the decision to create policies per group (Entity) may be too granular for current requirements and lead to maintenance issues and implementation complexity.
We need to assign policies to each group.



As explained in [this](https://dzone.com/articles/simple-attribute-based-access-control-with-spring) post, we can use Spring EL to define policies and decouple policies from groups. 
That is, defining a set of policies for all users/groups. 
If custom policies are required, E.g. per business, we can add flags to the context and use them in the policy check.

An example of a Policy rule would be something like:
```
    - name: Allow operator to retrieve attachment
      description: User with role OPERATOR can retrieve attachments if user belongs to sender or recipient group
      target: action == 'RETRIEVE' && target instanceof T(eu.europa.ec.etrustex.web.persistence.entity.Attachment)
      condition: principal.hasAuthority('OPERATOR', target.message.senderGroup.id) || environment[isRecipient]
```

That means significant refactoring and the following performance issues shall be handled by;
- Caching access decisions and policies
- Using compiled SpEL



After discussion with FUSCO Emanuele, for the moment, while no Business/Entity specific requirements exist for authorization, we can just use one set of policies in the way we do today and apply them to all groups.

If/when we have group-specific requirements we can consider refactor the access control implementation.
