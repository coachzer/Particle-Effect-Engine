# Particle Effect Engine - Project for the course: Programming 3 
*by Nikola Kovačević*  

## Abstract
In this report, a particle effect engine is described. The engine consists of a particle emitter and particles with limited lifespans. The emitter constantly emits particles vertically. The report discusses the implementation, results, and benchmarks of the engine.

## Introduction
A particle system is composed of an emitter and short-lived particles. The emitter determines the rate and location of particle emission, while the particles define their movement and removal. The visual impact of the system is determined by the emission style and appearance of the particles.

## Approach
The particle system is implemented using the JavaFX framework. The emitter is positioned as a horizontal line in the center of the application, and particles are emitted vertically. Each particle has properties such as location, velocity, acceleration, width, height, time to live (TTL), and color changes based on TTL. The implementation also includes attract and repel nodes.

## Implementation
The application is developed using Java and JavaFX framework. The code includes the implementation of the emitter, particles, and attract/repel nodes. The emitter continuously emits particles, and each particle has its own properties. The implementation leverages JavaFX features for adding and removing particles from the scene.

## Results
The final application consists of a particle emitter, attract/repel nodes, and a frames per second (FPS) counter. The average FPS is calculated at the end of the program execution. The report presents the visual representation of the application and console logs.

## Benchmarks
The benchmarks include tests conducted with different numbers of particles. Sequential and parallel run modes are evaluated. The tests measure the average FPS for each iteration. The results show that the sequential mode experiences performance drop-off when the number of particles exceeds 10,000, while the parallel mode maintains consistent performance.

## Conclusion
The implemented particle effect engine runs efficiently with a small number of particles in both sequential and parallel modes. However, when the number of particles increases significantly, the sequential mode experiences performance degradation, while the parallel mode maintains smooth execution.
