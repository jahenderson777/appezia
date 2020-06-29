(ns appezia.views
  (:require
   [re-frame.core :as re-frame]
   [clojure.string :as str]
   [appezia.subs :as subs]))

(defn viewport-size []
  {:width js/document.documentElement.clientWidth
   :height js/document.documentElement.clientHeight})

(defn hsl
  ([hue sat lightness]
   (hsl hue sat lightness 1))
  ([hue sat lightness alpha]
   (str "hsla(" (mod hue 360) ", " sat "%, " lightness "%, " alpha ")")))

(def M "M")
(def L "L")
(def H "H")
(def V "V")
(def C "C")
(def Z "Z")

(defn trapezium [hue sat li height
                 start-top-x start-bottom-x top-length bottom-length]
  [:path {;:transform "translate(70,0)"
          :style {:fill (hsl hue sat li)}
          :d (apply str (interpose " " [M start-top-x 0
                                        L (+ start-top-x top-length 3) 0
                                        L (+ start-bottom-x bottom-length 3) height
                                        L start-bottom-x height
                                        Z]))
          #_"M150 0 L75 200 L225 200 Z"}]
  )


(defn trapezium-strip [base-hue sat li height y]
  [:svg {:height height
         :y y
         :width "100%"}
   [:defs [:symbol {:id (str "strip" base-hue)}
           (loop [i 87
                  hue 0
                  top -8
                  bottom -8
                  shapes [:g]]
             (if (= 0 i)
               shapes
               (let [t (* 3 (+ 1 5 #_(rand-int 14)))
                     b (* 3 (+ 1 4 #_(rand-int 14)))]
                 (recur (dec i)
                        (+ hue 71)
                        (+ t top) (+ b bottom) (conj shapes [trapezium (+ base-hue (mod hue 100) i)
                                                             sat li
                                                             height
                                                             top
                                                             bottom
                                                             t b])))))]]
   [:use {:href (str "#strip" base-hue)
          :x 0 :y 0
          :transform (str "translate(" (/ @(re-frame/subscribe [:get :width]) 2) ",0) scale(" (* 1.0 (/ (+ 213 @(re-frame/subscribe [:get :scroll-top])) 200.9)) ",1)")
          }]
   [:use {:href (str "#strip" base-hue)
          :x 0 :y 0
          :transform (str "translate(" (+ 2 (/ @(re-frame/subscribe [:get :width]) 2)) ",0) scale(" (* -1.0 (/ (+ 213 @(re-frame/subscribe [:get :scroll-top])) 200.9)) ",1)")
          }]
   #_[trapezium 100 200 0 100 200 300]])

(defn scroll-mix [start end scroll-start scroll-end]
  (let [scroll-pos @(re-frame/subscribe [:get :scroll-top])]
    (if (< scroll-pos scroll-start)
      start
      (if (> scroll-pos scroll-end)
        end
        (let [scroll-range (- scroll-end scroll-start)
              y1 (- scroll-pos scroll-start)
              ratio (/ y1 scroll-range)]
          (+ start (* ratio (- end start))))))))

(defn width-mix [start end width-start width-end]
  (let [width @(re-frame/subscribe [:get :width])]
    (if (< width width-start)
      start
      (if (> width width-end)
        end
        (let [width-range (- width-end width-start)
              x1 (- width width-start)
              ratio (/ x1 width-range)]
          (+ start (* ratio (- end start))))))))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        strip-height 68
        strip-scale (scroll-mix 1.0 0.16 0 400)
        logo-width (width-mix 280 420 320 820)]
    [:div ;{:style {:background-color (hsl 180 40 94)}}
     [:div {:style {:position "fixed"
                    :top 0
                    :left 0
                    ;:z-index -100
                    :width "100%"
                    :height "100%"}}
      [:div.logo {:style {:text-align "center"
                          :top (scroll-mix 351 10 0 400)
                          }}
       [:img {:width logo-width
              :style {:margin-left (* logo-width 0.1)
                      :left "50%"}
              :src "logo.png"}]
       [:p.strap {:style {:line-height "1rem"
                    :color "#88a"}}
        "rapid development of mobile apps, web apps / websites, software & technological solutions."]]
      [:svg {:height (* 7 strip-height)              
             :width "100%"}
       [:g {:transform (str "scale(1, 1" ;(* 1.0 strip-scale) 
                            ")")}
        [trapezium-strip 11 73 80 strip-height (* 0 strip-height strip-scale)]
        [trapezium-strip 33 65 84 strip-height (* 1 strip-height strip-scale)]
        [trapezium-strip 66 60 88 strip-height (* 2 strip-height strip-scale)]
        [trapezium-strip 77 55 92 strip-height (* 3 strip-height strip-scale)]
        [trapezium-strip 99 50 94 strip-height (* 4 strip-height strip-scale)]
        [trapezium-strip 140 45 96 strip-height (* 5 strip-height strip-scale)]
        [trapezium-strip 180 40 97 strip-height (* 6 strip-height strip-scale)]]]
      
      ]
     [:div.content {:style {:padding-top 545}}
      
      #_#_[:h1.logo "appezia"]
        [:h2.logo {:style {:position "relative"
                           :top -10
                           :left 86
                           :padding-bottom 200}}
         "technologies"]
      
      [:div {:style {:background-color "#000"
                     :border "1px solid #ccc"
                     :padding 20}}
       [:h2 {:style {:padding-top 0}}
        "COVID-19"]
       [:p "Is your business/organisation having to change due to COVID-19 restrictions?" [:br]
        "We can quickly develop bespoke booking & ordering systems, that can not only enable a restoration of business but add extra value as well."]]
      [:h2 "Mobile apps"]
      [:p "Simultaneous development of iOS and Android apps."]
      [:h2 "Web apps / Websites"]
      [:p "From simple websites to complex bespoke web applications."]
      [:h2 "Software & technological solutions"]
      [:p "Back-office utilities, warehouse/production systems, custom hardware and electronics such as integrated weigh scales & sensors."]
      
      [:h1 "How we work"]
      [:h2 "Visioning"]
      [:p "You know your business. We know what we can achieve with technology. We seek to get a deep understanding of your processes so that we can work with you to expand and refine your dreams of what you thought technically possible."]
      [:h2 "Rapid prototyping"]
      [:p "To give confidence in our ability to deliver, and to further evolve the vision for your solution we get to work quickly developing a prototype. Rather than employing a sales team to convince you we can deliver our vision we let our prototypes speak for themselves."]
      [:h2 "We commit early, you commit late"]
      [:p "Only after we have demonstrated a prototype that satisfies the core functionality of your solution do we ask for your full commitment."]
      
     
      ]
     [:div.footer
      [:div.footer-inner
       [:h1 "Contact"]
       [:h3 "Tel: 07912 097088"]
       [:h3 "Email: jahenderson777@gmail.com"]
       [:h3 "Address: Appezia, 6 Buckfast Rd, Buckfastleigh, Devon, TQ11 0EA"]]]
     ]))
