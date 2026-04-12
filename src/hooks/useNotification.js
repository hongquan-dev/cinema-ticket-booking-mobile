import { useState } from 'react';
import NotificationDialog from '../components/notification/NotificationDialog';

export default function useNotification() {
    const [options, setOptions] = useState(null);

    const notify = (title, message, type = 'info') => {
        return new Promise((resolve) => {
            setOptions({ title, message, type, resolve });
        });
    };

    const handleClose = (result) => {
        if (options) options.resolve(result);
        setOptions(null);
    };

    const NotificationComponent = options ? (
        <NotificationDialog
            title={options.title}
            message={options.message}
            type={options.type}
            onClose={handleClose}
        />
    ) : null;

    return { notify, NotificationComponent };
}