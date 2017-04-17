# szz
SZZ is an algorithm to locate fix-inducing changes. Fix inducing-changes is a change that later gets undone by a fix.

### Steps in the szz algorithm
  Supose that _r1_ is a revision to fix a bug, and _r2_ is a revision before _r1_.
  
  - Use a diff command between _r1_ and _r2_ to locating the lines _l_ touched by a bug.
  - Use a anotate command in _r1_ to check the most recent version that touch each line in _l_.
  - Investigate suspect revisions, to remove those that not contribute to the failure.
  
