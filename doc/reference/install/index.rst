.. module:: unredd.install

=============================
Installing NFMS in Production
=============================

This module describes the step by step installation and configuration of the nfms platform, as needed in production environments.

The NFMS system is developed in java, so it can be run on top of different operating systems. Nevertheless, Ubuntu or CentOS Linux distributions are recommended.

There are two possible scenarios for the installation:

- Installation of the two subsystem (staging and dissemination) in the same machine: normally for trainings.
- Installation of staging and dissemination in two different machines: typically in production environments.

Along the following documentation, installation steps that specifically apply to only one of the two subsystem will specify this clearly. Therefore, steps that do not indicate anything apply to the installation of both subsystems.

The installation has been fully tested on Ubuntu 12.04

.. note:

   This step by step guide is based on the `Ubuntu 12.04 <http://www.ubuntu.com/>`_ Linux distribution.
   The exact steps may vary depending on your Operating System, distribution and version.


In this module you will see:

.. toctree::
   :maxdepth: 2 

   hw_requirements
   sw_prerequisites
   geoserver
   admin
   portal
   stats-indicator
