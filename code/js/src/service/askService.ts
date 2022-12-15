import { useState, useEffect } from 'react'
import { Service } from './Service'

export function askService(
    service: Service, 
    serviceFunction: (...args: any[]) => Promise<any | undefined>,
    ...args: any[]
): any | undefined {
    const [content, setContent] = useState(undefined)
    useEffect(() => {
        let cancelled = false
        async function doService() {
            const resp = await serviceFunction.call(service, args)
            if (!cancelled) {
                setContent(resp)
            }
        }
        setContent(undefined)
        doService()
        return () => {
            cancelled = true
        }
    }, [serviceFunction])
    return content
}