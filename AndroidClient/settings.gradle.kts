rootProject.name = "AndroidClient"
include("app")
include(":DTO")
project(":DTO").projectDir = File("./../Server/DTO")