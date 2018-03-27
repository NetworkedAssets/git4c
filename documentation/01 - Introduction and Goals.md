# 1. Getting Started


Git4C is a confluence plugin that allows you to keep your documentation by code in git repository and view it via Git4C macro in confluence.


## 1.1 Requirements Overview


- [Atlassian SDK](https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project)
- Confluence (from version 5.8.2)
- IntelliJ IDEA (Community version would be enough if you won't edit JS - in that case Ultimate edition or VSCode is recommended)

## 1.2 Quality Goals

Defined process of delivery flow during development of Git4C is giving clear and easy way to understand each phase of work by all team members.

![](./images/1.2%20Flow%20Diagram.puml)


### 1.2.1 Specification 

Written in BDD (using Gherkin) specification for each Feature with a list of Scenarios defines how plugin works.

### 1.2.2 Development 
Development process is a part of global delivery process that is described at [1.2 Quality Goals](../1.2 Quality Goals/Quality Goals).  
  
It's minor goals are:
 - Deliver clean and readable code
 - Product with stable features
 - Minimize possible regressions
 - Readable documentations

#### Code
There are few rules regarding code style:
 - Code should be readable
 - There should be no comments in code if there is no such need
 - Unused code should be removed
 - Method should have no more then 10 lines if there is such a possibility
 - Variables and Methods should have readable names

#### Merge Requests 
Every request from `feature/**` or `bug/**` or `hotfix/**` branch should be described also by Merge Request Description. See details at: [10.xx Merge Request Template](1.2.2.1%20-%20Merge%20Requrests%20Template.md)

### 1.2.3 Testing 

There are few stages of testing and many layers of tests. Most important are: 
 - Use Case tests 
 - Module Tests
 - API Tests
 - Functional Tests (implementation of BDD scenarios tests)
 - Acceptance Tests (manual)
 
### 1.2.4 Deployment

Each version of plugin that is deploy to target environment should have complete test suite and must be accepted by Product Owner, Project Manager, Team Leader and owner of the product.  
Readable report from Functional Tests should be present in deployment process for review.

## 1.3 Stakeholders

### Project description	
The goal of this plugin is to deliver documentation fragments from code to improve process of documentation.
### Deadlines	
Q1 / 2018

### Background
There are plenty documentations in Confluence and they are often not up-to-date. Goal is to make simplier to deliver documentation that may be kept in the code. In addition there should be introduced process with documentation review and delivery. Use CI / CD to deliver documentation from Git. 

### Key Stakeholders  
<b>Client:</b> NetworkedAssets (Grzegorz Kopij)  
<b>Technical Architect:</b> Bartosz Bednarek  
<b>Delivery Manager:</b> Vladyslava Daruk  
<b>Technical Analyst:</b> Bartosz Bednarek  
<b>Developers:</b> Adnrzej Ressel, Kamil Urban, Bartosz Bednarek  
<b>Project Owner:</b> Rafał Sumisławski  


