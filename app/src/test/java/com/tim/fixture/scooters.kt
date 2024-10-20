class Fixtures {
    companion object {
        val scooters = """
    {
      "name": "Stockholm",
      "scooters": [
        {
          "id": 1,
          "name": "Scooter A1",
          "battery": 1.0,
          "in_use": true,
          "need_fix": false,
          "total_rides": 3000
        },
        {
          "id": 2,
          "name": "Scooter A2",
          "battery": 0.5,
          "in_use": true,
          "need_fix": false,
          "total_rides": 23
        },
        {
          "id": 3,
          "name": "Scooter B3",
          "battery": 0.32,
          "in_use": false,
          "need_fix": true,
          "total_rides": 80
        },
        {
          "id": 4,
          "name": "Scooter B4",
          "battery": 1.0,
          "in_use": true,
          "total_rides": 12
        },
        {
          "id": 5,
          "name": "Scooter C5",
          "battery": 0.8,
          "in_use": false,
          "need_fix": false,
          "total_rides": 534
        },
        {
          "id": 6,
          "name": "Scooter C6",
          "battery": 0.75,
          "in_use": false,
          "need_fix": true
        },
        {
          "id": 7,
          "name": "Scooter D7",
          "battery": 0.12,
          "in_use": false,
          "need_fix": false,
          "total_rides": 800
        },
        {
          "id": 8,
          "name": "Scooter D8",
          "battery": 0.1,
          "in_use": false,
          "need_fix": true,
          "total_rides": 600
        },
        {
          "id": 9,
          "name": "Scooter E9",
          "battery": 0.87,
          "in_use": false,
          "need_fix": false,
          "total_rides": 632
        },
        {
          "id": 10,
          "name": "Scooter E10",
          "battery": 0.67,
          "in_use": false,
          "need_fix": false
        },
        {
          "id": 11,
          "name": "Scooter F11",
          "battery": 0.43,
          "in_use": true,
          "need_fix": false,
          "total_rides": 873
        },
        {
          "id": 12,
          "name": "Scooter F12",
          "battery": 0.0,
          "in_use": false,
          "need_fix": false,
          "total_rides": 434
        }
      ]
    }
""".trimIndent()
    }
}
