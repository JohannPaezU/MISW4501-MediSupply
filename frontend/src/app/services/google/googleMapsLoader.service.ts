import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class GoogleMapsLoaderService {
  private static promise: Promise<any>;
  private static isLoaded = false;

  loadGoogleMaps(): Promise<any> {
    if (GoogleMapsLoaderService.isLoaded) {
      return Promise.resolve(window);
    }

    if (GoogleMapsLoaderService.promise) {
      return GoogleMapsLoaderService.promise;
    }

    GoogleMapsLoaderService.promise = new Promise((resolve, reject) => {
      const script = document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${environment.googleMapsApiKey}&libraries=geometry`;
      script.async = true;
      script.defer = true;

      script.onload = () => {
        GoogleMapsLoaderService.isLoaded = true;
        resolve(window);
      };

      script.onerror = (error) => {
        reject(error);
      };

      document.head.appendChild(script);
    });

    return GoogleMapsLoaderService.promise;
  }

  isGoogleMapsLoaded(): boolean {
    return GoogleMapsLoaderService.isLoaded;
  }
}
