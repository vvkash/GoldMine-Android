package com.goldmine.uncc.data.model

/** Student discount, ported from the iOS `Discount.samples` catalogue. */
data class Discount(
    val name: String,
    val description: String,
    val websiteUrl: String,
) {
    companion object {
        val samples: List<Discount> = listOf(
            Discount("Apple", "Up to 10% off", "https://www.apple.com/us-edu/store"),
            Discount("Adobe", "60% off Creative Cloud", "https://www.adobe.com/creativecloud/buy/students.html"),
            Discount("Amazon Prime", "6-month free trial", "https://www.amazon.com/amazonprime"),
            Discount("Spotify", "50% off Premium", "https://www.spotify.com/us/student/"),
            Discount("Microsoft", "Office 365 for free", "https://www.microsoft.com/en-us/education/products/office"),
            Discount("Nike", "10% off all purchases", "https://www.nike.com/us/student-discount"),
            Discount("YouTube Premium", "50% off subscription", "https://www.youtube.com/premium/student"),
            Discount("Best Buy", "Student deals program", "https://www.bestbuy.com/site/electronics/college-student-deals/pcmcat276200050000.c"),
            Discount("Hulu", "\$1.99/month for students", "https://www.hulu.com/student"),
            Discount("Samsung", "Up to 30% off devices", "https://www.samsung.com/us/shop/discount-program/education/"),
            Discount("Lenovo", "Extra 5% off laptops", "https://www.lenovo.com/us/en/student/"),
            Discount("Headspace", "\$9.99/year meditation app", "https://www.headspace.com/studentplan"),
            Discount("Canva Pro", "Free for students", "https://www.canva.com/education/"),
            Discount("GitHub", "Student Developer Pack", "https://education.github.com/pack"),
            Discount("Grammarly", "50% off Premium", "https://www.grammarly.com/edu/students"),
            Discount("JetBrains", "Free IDE access", "https://www.jetbrains.com/community/education/"),
            Discount("The New York Times", "\$1/week digital access", "https://www.nytimes.com/subscription/education/student"),
            Discount("Notion", "Free Pro plan", "https://www.notion.so/product/notion-for-education"),
            Discount("UNiDAYS", "Student discount hub", "https://www.myunidays.com/US/en-US"),
            Discount("Student Beans", "Discount marketplace", "https://www.studentbeans.com/us"),
        )
    }
}

/** Campus dining venue, ported from the iOS `EatsView` list. */
data class DiningVenue(
    val id: Int,
    val name: String,
    val description: String,
    val hours: String,
    val location: String,
) {
    companion object {
        val all: List<DiningVenue> = listOf(
            DiningVenue(1, "Crown Commons", "All-you-care-to-eat dining", "7:00 AM - 8:00 PM", "Student Union"),
            DiningVenue(2, "SoVi", "All-you-care-to-eat dining", "7:00 AM - 8:00 PM", "South Village Crossing"),
            DiningVenue(3, "Chick-fil-A", "Fast food", "10:30 AM - 7:00 PM", "Student Union"),
            DiningVenue(4, "Starbucks", "Coffee and snacks", "7:00 AM - 9:00 PM", "Student Union"),
            DiningVenue(5, "Panda Express", "Chinese cuisine", "10:30 AM - 7:00 PM", "Student Union"),
            DiningVenue(6, "Bojangles", "Fast food", "10:30 AM - 7:00 PM", "Student Union"),
            DiningVenue(7, "Wendy's", "Fast food", "10:30 AM - 7:00 PM", "Student Union"),
        )
    }
}
