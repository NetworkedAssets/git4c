## What does this MR do?

(briefly describe what this MR is about)

## Checklist for the author

- Documentation updated (including BDD):
  - [ ] done
  - [ ] not needed
- Integration tests added / adapted
  - [ ] done
  - [ ] not needed
- UI tests added / adapted
  - [ ] done
  - [ ] not needed
- Team informed about breaking changes (altered DB schema, some new rules, way of building etc.)
  - [ ] done
  - [ ] no breaking changes this time
- In case of changes in DB schemas of the core modules, is there a migration script present?
  - [ ] done
  - [ ] no changes in core modules' schema were made
- [ ] Acceptance criteria in Jira ticket filled-in and still up-to-date?!
- [ ] Jira ticket moved to `Review`?
- [ ] Potential reviewers informed about new MR
- [ ] Branch merged with / rebased on current develop?

**NOTE** If any of the points above is not finished, consider marking the MR with the WIP prefix

## Checklist for the reviewer

- [ ] CI pipeline passed
- [ ] If UI changes were made - relevant UI test passed
- [ ] Check if actions marked as `not needed` are really not needed
- [ ] Changes made in the source code are OK

## Checklist before merging by the author
- [ ] Source branch will be removed
- [ ] At least 2 :thumbsup: earned
- [ ] Jira ticket moved to `To Test` or to `Done`?
