# 🚧 Line segments intersection via Balaban's algorithm.

## Overview
Reporting of segment set intersections is one of the
fundamental problems of computational geometry. This library implements *Ivan J. Balaban*'s [*Intermediate Algorithm*](https://www2.cs.sfu.ca/~binay/813.2011/Balaban.pdf) for finding intersecting segment pairs from a given set of N segments in the plane.

This algorithm has complexity *O( n\*log<sup>2</sup>(n) + k )* (where k is the number of intersecting pairs), which is **much** faster than the more commonly implemented *O( (n+k)\*log(n) )* *Bentley–Ottmann* algorithm for the same problem. 

a more uqibitous implementation for this problem. In practice therefore, this library processes segment sets 100x faster on sizable inputs compared to existing (Java) Bentley–Ottmann implementations (i.e. long segments each with many intersections). Datasets with relatively short segments see a less pronounced (though still very much considerable: perhaps 10x) performance benefit. 

This repository improves the abandoned original project from *Google Code Archive* whose author was [Taras](https://github.com/taarraas), and provides via Jitpack for Maven & Gradle for easy use in other projects.

## Algorithm

Balaban's applies the sweepline algorithm 

## Input

A single set (for self intersection), 

~~or two sets (i.e. "which edges from A intersect with edges from B?", which could be used to compute the polygonal intersection for example).~~

The algorithm itself does not check for or handle *degenerate* inputs and will error (generally a stack overflow) on such inputs. Yet the library offers a method to filter out before processing.

Segment sets are degenerate when:
- A segment is vertical (its vertices have the same y coordinate)

However you can clean the input using the 

## Usage

The algorithm does not natively compute **points** of intersection. Rather, it returns intersecting **segment pairs**. returns for O(1).

You must provide a callback function to the solver: *it* provides intersecting pairs — *you* decide what to do with them.

```
code
```