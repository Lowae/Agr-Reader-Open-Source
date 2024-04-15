package com.lowae.agrreader.ui.svg.illustrations

private var _agrPro: String? = null

val Illustrations.AgrPro: String
    get() {
        if (_agrPro != null) {
            return _agrPro!!
        }
        _agrPro =
            """<svg width="66" height="66" viewBox="0 0 66 66" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="33" cy="33" r="33" fill="#FAFAF2" />
    <circle cx="33" cy="33" r="32.75" stroke="#E0E4D6" stroke-opacity="0.6" stroke-width="1" />
    <path
        d="M34.9746 21.4795C36.1842 20.7915 37 19.491 37 18C37 15.7909 35.2092 14 33 14C30.7908 14 29 15.7909 29 18C29 19.4944 29.8194 20.7974 31.0336 21.4841L31.0156 21.5207C29.7042 24.2539 27.8846 28.1695 25.0733 29.6395C22.7667 30.8456 19.4526 30.2388 16.9927 29.7888C16.8843 28.2305 15.5859 27 14 27C12.3431 27 11 28.3431 11 30C11 31.4694 12.0563 32.692 13.451 32.9498L19.3453 48.1672C20.2401 50.4774 22.4628 52 24.9402 52H41.0598C43.5372 52 45.7598 50.4774 46.6548 48.1672L52.549 32.9498C53.9436 32.692 55 31.4694 55 30C55 28.3431 53.6568 27 52 27C50.4446 27 49.1656 28.1837 49.0148 29.6995C46.4966 30.026 43.2502 30.4426 40.9268 29.2277C38.1718 27.7873 36.3268 24.1415 34.9746 21.4795Z"
        fill="url(#paint0_linear_9129_775)" />
    <defs>
        <linearGradient id="paint0_linear_9129_775" x1="19.5" y1="21.5" x2="45.5" y2="52"
            gradientUnits="userSpaceOnUse">
            <stop offset="0.114583" stop-color="<material>p90</material>" />
            <stop offset="0.508737" stop-color="<material>s90</material>" />
            <stop offset="0.864583" stop-color="<material>t90</material>" />
        </linearGradient>
    </defs>
</svg>"""
        return _agrPro!!
    }
