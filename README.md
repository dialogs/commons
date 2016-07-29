# Dialog Commons

This project contains code which Dialog team uses in its projects.

If you with to add all common libraries, put the following to your build.sbt:

```
libraryDependencies += "im.dlg" %% "dlg-commons" % 0.0.6
```

## Concurrent

Contains code which helps to deal with concurrency.

* [ActorFutures](https://github.com/dialogs/commons/blob/master/dialog-concurrent/src/main/scala/im/dlg/concurrent/ActorFutures.scala) trait helps to handle Future result in Actor's receive loop.
* [FutureExt](https://github.com/dialogs/commons/blob/master/dialog-concurrent/src/main/scala/im/dlg/concurrent/FutureExt.scala) contains function for to processing sequence of futures in sequential order.

```
libraryDependencies += "im.dlg" %% "dlg-concurrent" % 0.0.6
```
